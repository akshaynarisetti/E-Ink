package com.example.e_ink.activities.activities


import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.e_ink.R
import kotlinx.android.synthetic.main.skeleton_images.view.*

class ImageAdapter (var images : MutableList<Uri?>) : RecyclerView.Adapter<ImageAdapter.imageViewHolder>() {

    inner class imageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): imageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.skeleton_images,parent,false)
        return imageViewHolder(view)
    }

    override fun getItemCount(): Int {

        return images.size
    }

    override fun onBindViewHolder(holder: imageViewHolder, position: Int) {
        holder.itemView.apply {
            var d: Uri? = images[position]
            imageView.setImageURI(d)
            // imageView.setImageBitmap(images[position])

        }
    }

}