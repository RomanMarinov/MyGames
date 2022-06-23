package com.dev_marinov.mygames.presentation.games

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dev_marinov.mygames.R
import com.dev_marinov.mygames.data.Games
import com.dev_marinov.mygames.databinding.ItemGamesBinding
import com.squareup.picasso.Picasso

class GamesAdapter : RecyclerView.Adapter<GamesAdapter.ViewHolder>(){

    var hashMap: HashMap<Int, Games> = HashMap()

    // метод для обновления списка из вне
    fun setUpdateData(hashMap: HashMap<Int, Games>) {
        this.hashMap = hashMap
    }

    lateinit var mListener: onItemClickListener

    interface onItemClickListener {
        fun onItemClick(position: Int)
    }
    fun setOnItemClickListener(listener: onItemClickListener) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val listItemBinding = ItemGamesBinding.inflate(inflater,parent,false)
        return ViewHolder(listItemBinding, mListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(hashMap[position])
    }

    override fun getItemCount() = hashMap.size

    inner class ViewHolder (private val binding: ItemGamesBinding, listener: onItemClickListener)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(games: Games?) {
            binding.itemGames = games

            Picasso.get()  // установка главной картинки игры
                .load(games!!.background_image.toString())
                .resize(500, 300) // обязательно свои размеры (т.к. оригинал большой)
                .placeholder(R.drawable.picture_not_available)
                .centerCrop()
                .into(binding.imgMain) // -----> картинка

            // Метод executePendingBindings используется, чтобы биндинг не откладывался,
            // а выполнился как можно быстрее. Это критично в случае с RecyclerView.
            binding.executePendingBindings() // обязательно
        }

        init {
            itemView.setOnClickListener {
                listener.onItemClick(bindingAdapterPosition)
            }
        }
    }

}