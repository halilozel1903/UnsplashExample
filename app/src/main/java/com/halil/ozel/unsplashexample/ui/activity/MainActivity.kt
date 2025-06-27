package com.halil.ozel.unsplashexample.ui.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.halil.ozel.unsplashexample.databinding.ActivityMainBinding
import com.halil.ozel.unsplashexample.ui.adapter.ImageAdapter
import com.halil.ozel.unsplashexample.ui.viewmodel.ImageViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val imageAdapter = ImageAdapter()
    private lateinit var linearLayoutManager: LinearLayoutManager
    private val viewModel: ImageViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupView()
        observeImages()
    }

    private fun setupView() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.recyclerView.apply {
            adapter = imageAdapter
            layoutManager = LinearLayoutManager(this@MainActivity).also {
                this@MainActivity.linearLayoutManager = it
            }
            setHasFixedSize(true)
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy > 0) {
                        val visibleItemCount = this@MainActivity.linearLayoutManager.childCount
                        val totalItemCount = this@MainActivity.linearLayoutManager.itemCount
                        val pastVisibleItems = this@MainActivity.linearLayoutManager.findFirstVisibleItemPosition()
                        if (visibleItemCount + pastVisibleItems >= totalItemCount) {
                            viewModel.loadNextPage()
                        }
                    }
                }
            })
        }
    }

    private fun observeImages() {
        viewModel.responseImages.observe(this) { response ->
            response?.let { imageAdapter.submitList(it) }
        }
    }
}
