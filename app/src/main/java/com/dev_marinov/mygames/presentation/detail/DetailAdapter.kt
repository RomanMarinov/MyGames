package com.dev_marinov.mygames.presentation.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dev_marinov.mygames.databinding.ItemGamesDetailBinding
import com.dev_marinov.mygames.domain.screenshot.ScreenShotsImages
import com.squareup.picasso.Picasso

class DetailAdapter : ListAdapter<ScreenShotsImages, DetailAdapter.ViewHolder>(DetailDiffUtilCallback()){

    private var screenShotsImages: List<ScreenShotsImages> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val listItemBinding = ItemGamesDetailBinding.inflate(inflater,parent,false)
        return ViewHolder(listItemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(screenShotsImages[position].image)
        }

    override fun submitList(list: List<ScreenShotsImages>?) {
        super.submitList(list)
        list?.let { this.screenShotsImages = it.toList() }
    }

    override fun getItemCount(): Int {
        return screenShotsImages.size
    }

    class ViewHolder (private val binding: ItemGamesDetailBinding) : RecyclerView.ViewHolder(binding.root) {
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

class DetailDiffUtilCallback : DiffUtil.ItemCallback<ScreenShotsImages>() {
    override fun areItemsTheSame(oldItem: ScreenShotsImages, newItem: ScreenShotsImages): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: ScreenShotsImages, newItem: ScreenShotsImages): Boolean {
        return oldItem == newItem
    }

}
