package com.example.autokiosk.presentation.product.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.autokiosk.databinding.ItemImageGalleryBinding

class ImageGalleryAdapter(
    private val context: Context,
    private val imageUrls: List<String>
) : RecyclerView.Adapter<ImageGalleryAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemImageGalleryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        Glide.with(context)
            .load(imageUrls[position])
            .into(holder.binding.productImageView)
    }

    override fun getItemCount(): Int = imageUrls.size

    inner class ImageViewHolder(val binding: ItemImageGalleryBinding) : RecyclerView.ViewHolder(binding.root)
}
