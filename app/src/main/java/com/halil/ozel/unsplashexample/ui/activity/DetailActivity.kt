package com.halil.ozel.unsplashexample.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.halil.ozel.unsplashexample.databinding.ActivityDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {
	private lateinit var binding: ActivityDetailBinding

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityDetailBinding.inflate(layoutInflater)
		setContentView(binding.root)

		setSupportActionBar(binding.toolbar)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		binding.toolbar.setNavigationOnClickListener { finish() }

		val imageUrl = intent.getStringExtra("image_url")
		val desc = intent.getStringExtra("image_desc")

		binding.detailImage.load(imageUrl) {
			placeholder(com.halil.ozel.unsplashexample.R.drawable.placeholder)
			error(com.halil.ozel.unsplashexample.R.drawable.placeholder)
			crossfade(true)
		}

		binding.detailDescription.text = desc ?: ""
	}
}


