package com.halil.ozel.unsplashexample.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.halil.ozel.unsplashexample.R
import com.halil.ozel.unsplashexample.databinding.ImageItemBinding
import com.halil.ozel.unsplashexample.model.ImageItem

class ImageAdapter(private val onItemClick: (ImageItem) -> Unit = {}) : ListAdapter<ImageItem, ImageAdapter.ImageViewHolder>(DIFF_CALLBACK) {

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
            val imageLink = currImage.urls.regular.takeIf { it.isNotBlank() }
                ?: currImage.urls.small.takeIf { it.isNotBlank() }
            imageView.load(imageLink) {
                placeholder(R.drawable.placeholder)
                error(R.drawable.placeholder)
                fallback(R.drawable.placeholder)
                crossfade(true)
            }
            imageView.contentDescription = currImage.description
                .takeIf { it.isNotBlank() }
                ?: root.context.getString(R.string.image_content_description)
            root.setOnClickListener { onItemClick(currImage) }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ImageItem>() {
            override fun areItemsTheSame(oldItem: ImageItem, newItem: ImageItem) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: ImageItem, newItem: ImageItem) =
                oldItem == newItem
        }
    }
}
