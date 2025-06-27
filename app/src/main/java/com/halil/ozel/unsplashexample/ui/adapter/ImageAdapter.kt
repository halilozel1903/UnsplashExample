package com.halil.ozel.unsplashexample.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.halil.ozel.unsplashexample.databinding.ImageItemBinding
import com.halil.ozel.unsplashexample.model.ImageItem

class ImageAdapter : ListAdapter<ImageItem, ImageAdapter.ImageViewHolder>(DIFF_CALLBACK) {

    class ImageViewHolder(val binding: ImageItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(
            ImageItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val currImage = getItem(position)

        holder.binding.apply {
            val imageLink = currImage.urls.full
            imageView.load(imageLink) {
                crossfade(true)
                crossfade(DURATION_MILLIS)
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ImageItem>() {
            override fun areItemsTheSame(oldItem: ImageItem, newItem: ImageItem) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: ImageItem, newItem: ImageItem) =
                oldItem == newItem
        }

        private const val DURATION_MILLIS = 1000
    }
}
