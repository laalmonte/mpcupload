package com.android.sample.mpcupload

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class PhotoAdapter(private var mList: List<Photo>,
                    private var onItemClicked: ((photoParam: Photo) -> Unit)
) : RecyclerView.Adapter<PhotoAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_photo, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val photoObj = mList[position]

        // for bitmap from camera
//        holder.imagePhoto.setImageBitmap(photoObj.url)

        // for gallery
        holder.imagePhoto.setImageURI(photoObj.url)

        // for url from web
//        if (photoObj.url != ""){
//            Picasso
//                .get()
//                .load(photoObj.url)
//                .placeholder(R.drawable.ic_location)
//                .into(holder.imagePhoto)
//        }

        holder.imageDelete.setOnClickListener {
            onItemClicked(photoObj)
        }
    }

    fun updateList(newList: List<Photo>){
        mList = newList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int { return mList.size }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imagePhoto: ImageView = itemView.findViewById(R.id.ivPhoto)
        val imageDelete: ImageView = itemView.findViewById(R.id.ivDelete)
    }
}