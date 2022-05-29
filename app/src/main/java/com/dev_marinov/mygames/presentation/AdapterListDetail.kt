package com.dev_marinov.mygames.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.dev_marinov.mygames.R
import com.dev_marinov.mygames.data.Screenshots
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso

class AdapterListDetail(var newArrayScreenShots: MutableList<Screenshots>) : RecyclerView.Adapter<AdapterListDetail.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.rv_list_detail, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        Picasso.get()
            .load(newArrayScreenShots[position].image).memoryPolicy(MemoryPolicy.NO_CACHE)
            .placeholder(R.drawable.picture_not_available)
            .resize(500, 300)
            .centerCrop()
            .into(holder.imgDetail) // -----> картинка
        }

    override fun getItemCount(): Int {
        return newArrayScreenShots!!.size
    }

    class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgDetail: ImageView = itemView.findViewById(R.id.imgDetail)
    }

}