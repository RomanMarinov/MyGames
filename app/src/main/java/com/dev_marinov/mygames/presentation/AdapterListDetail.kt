package com.dev_marinov.mygames.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.dev_marinov.mygames.R
import com.dev_marinov.mygames.data.Screenshots
import com.dev_marinov.mygames.databinding.RvListDetailBinding
import com.squareup.picasso.Picasso

class AdapterListDetail(var newArrayScreenShots: MutableList<Screenshots>)
    : RecyclerView.Adapter<AdapterListDetail.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val listItemBinding = RvListDetailBinding.inflate(inflater,parent,false)
        return ViewHolder(listItemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(newArrayScreenShots[position].image)
        }

    override fun getItemCount(): Int {
        return newArrayScreenShots.size
    }

    class ViewHolder (private val binding: RvListDetailBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(image: String?) {

            Picasso.get()  // установка главной картинки игры
                .load(image)
                .resize(500, 300) // обязательно свои размеры (т.к. оригинал большой)
                //.placeholder(R.drawable.picture_not_available)
                .centerCrop()
                .into(binding.imgDetail) // -----> картинка
        }

    }

}