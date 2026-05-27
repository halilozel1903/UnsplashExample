package com.halil.ozel.unsplashexample.ui.activity

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.content.Intent
import androidx.appcompat.widget.SearchView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.halil.ozel.unsplashexample.R
import com.halil.ozel.unsplashexample.ui.activity.DetailActivity
import com.halil.ozel.unsplashexample.databinding.ActivityMainBinding
import com.halil.ozel.unsplashexample.ui.adapter.ImageAdapter
import com.halil.ozel.unsplashexample.ui.viewmodel.ImageViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val imageAdapter = ImageAdapter { item -> openDetail(item) }
    private lateinit var gridLayoutManager: GridLayoutManager
    private val viewModel: ImageViewModel by viewModels()
    private var isLoading = false
    private var hasItems = false
    private var hasResolvedInitialState = false
    private var latestErrorMessage: String? = null
    private var fullList: List<com.halil.ozel.unsplashexample.model.ImageItem> = emptyList()
    private var currentQuery: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupView()
        observeImages()
        observeLoadingState()
        observeErrorState()
    }

    private fun setupView() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.apply {
            adapter = imageAdapter
            gridLayoutManager = GridLayoutManager(this@MainActivity, 2)
            layoutManager = gridLayoutManager
            addItemDecoration(
                GridSpacingItemDecoration(
                    spanCount = 2,
                    spacing = resources.getDimensionPixelSize(R.dimen.grid_spacing)
                )
            )
            setHasFixedSize(true)
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy <= 0 || isLoading) return

                    val totalItemCount = gridLayoutManager.itemCount
                    val lastVisibleItem = gridLayoutManager.findLastVisibleItemPosition()
                    if (totalItemCount > 0 && lastVisibleItem >= totalItemCount - LOAD_MORE_THRESHOLD) {
                        viewModel.loadNextPage()
                    }
                }
            })
        }

        binding.retryButton.setOnClickListener {
            latestErrorMessage = null
            viewModel.loadNextPage()
        }

        binding.swipeRefresh.setOnRefreshListener {
            // Trigger a full refresh
            currentQuery = ""
            binding.searchView.setQuery("", false)
            viewModel.refresh()
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                currentQuery = query.orEmpty()
                applyFilter()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                currentQuery = newText.orEmpty()
                applyFilter()
                return true
            }
        })
    }

    private fun observeImages() {
        viewModel.responseImages.observe(this) { response ->
            if (response == null) return@observe

            hasResolvedInitialState = true
            val images = response
            fullList = images
            hasItems = images.isNotEmpty()
            applyFilter()
            binding.recyclerView.isVisible = hasItems
            updateEmptyState()
        }
    }

    private fun observeLoadingState() {
        viewModel.isLoading.observe(this) { loading ->
            isLoading = loading
            // Use shimmer for initial load (when no items yet), and progress indicator for subsequent loads
            val showShimmer = loading && !hasItems
            binding.shimmerLayout.isVisible = showShimmer
            if (showShimmer) binding.shimmerLayout.startShimmer() else binding.shimmerLayout.stopShimmer()
            binding.progressIndicator.isVisible = loading && hasItems
            // Reflect loading state in the swipe-to-refresh spinner
            binding.swipeRefresh.isRefreshing = loading
            updateEmptyState()
        }
    }

    private fun observeErrorState() {
        viewModel.errorMessage.observe(this) { message ->
            if (!message.isNullOrBlank()) {
                hasResolvedInitialState = true
            }
            latestErrorMessage = message
            if (message.isNullOrBlank()) return@observe

            if (hasItems) {
                Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
            }
            updateEmptyState()
        }
    }

    private fun updateEmptyState() {
        val showEmptyState = hasResolvedInitialState && !isLoading && !hasItems
        binding.emptyStateContainer.isVisible = showEmptyState

        if (showEmptyState) {
            binding.emptyStateTitle.text = getString(R.string.empty_state_title)
            binding.emptyStateMessage.text = latestErrorMessage ?: getString(R.string.empty_state_message)
            binding.retryButton.isVisible = latestErrorMessage != null
        }
    }

    private fun applyFilter() {
        val filtered = if (currentQuery.isBlank()) fullList else fullList.filter {
            (it.description ?: "").contains(currentQuery, ignoreCase = true)
        }
        imageAdapter.submitList(filtered)
    }

    private fun openDetail(item: com.halil.ozel.unsplashexample.model.ImageItem) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("image_url", item.urls.full)
        intent.putExtra("image_desc", item.description)
        startActivity(intent)
    }

    private class GridSpacingItemDecoration(
        private val spanCount: Int,
        private val spacing: Int
    ) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val position = parent.getChildAdapterPosition(view)
            if (position == RecyclerView.NO_POSITION) return

            val column = position % spanCount

            outRect.left = spacing - column * spacing / spanCount
            outRect.right = (column + 1) * spacing / spanCount
            if (position < spanCount) {
                outRect.top = spacing
            }
            outRect.bottom = spacing
        }
    }

    companion object {
        private const val LOAD_MORE_THRESHOLD = 4
    }
}
