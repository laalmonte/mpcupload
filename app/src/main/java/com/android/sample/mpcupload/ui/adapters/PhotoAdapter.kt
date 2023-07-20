package com.android.sample.mpcupload.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.android.sample.mpcupload.R
import com.android.sample.mpcupload.model.Photo

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

        // for bitmap from camera option
        holder.imagePhoto.setImageBitmap(photoObj.url)

        // for gallery option
//        holder.imagePhoto.setImageURI(photoObj.url)

        // for url from web option
//        if (photoObj.url != ""){
//            Picasso
//                .get()
//                .load(photoObj.url)
//                .placeholder(R.drawable.ic_location)
//                .into(holder.imagePhoto)
//        }

        // handle deleting
        holder.imageDelete.setOnClickListener {
            onItemClicked(photoObj)
        }
    }

    // for updating recycler
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