package com.elite.parking

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

/*class ImageAdapter(private val context: Context, private val imageUris: MutableList<Uri>) :
    RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val uri = imageUris[position]
        Glide.with(context)
            .load(imageUris[position])
            .placeholder(R.drawable.car3)
            .into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return imageUris.size
    }
  *//*  fun updateImages(newUri: String) {
        imageUris.add(newUri)
        notifyDataSetChanged()
    }*//*
}*/

class ImageAdapter(private val context: Context, private val imageUris: MutableList<Uri>) :
    RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        // Check if the imageUris list is not empty and display only the latest image
        if (imageUris.isNotEmpty()) {
            val latestImageUri = imageUris.last() // Get the latest image
            Glide.with(context)
                .load(latestImageUri)
                .placeholder(R.drawable.ic_default_image)
                .into(holder.imageView)
        }
    }

    override fun getItemCount(): Int {
        // Always return 1 since we only display the latest image
        return if (imageUris.isNotEmpty()) 1 else 0
    }

    // Optionally, you can update the adapter when a new image is added
    fun updateImages(newImageUri: Uri) {
        imageUris.clear() // Clear old images
        imageUris.add(newImageUri) // Add the latest image
        notifyDataSetChanged()
    }
}

