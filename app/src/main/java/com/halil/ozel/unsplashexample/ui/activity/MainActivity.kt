package com.halil.ozel.unsplashexample.ui.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.halil.ozel.unsplashexample.databinding.ActivityMainBinding
import com.halil.ozel.unsplashexample.ui.adapter.ImageAdapter
import com.halil.ozel.unsplashexample.ui.viewmodel.ImageViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var imageAdapter: ImageAdapter
    private val viewModel: ImageViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupImageData()
    }

    private fun setupImageData() {
        imageAdapter = ImageAdapter()
        binding.recyclerView.apply {
            adapter = imageAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
            setHasFixedSize(true)
        }
        viewModel.responseImages.observe(this) { response ->
            if (response != null) {
                imageAdapter.submitList(response)
            }
        }
    }
}