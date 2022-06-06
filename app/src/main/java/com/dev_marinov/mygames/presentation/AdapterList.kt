package com.dev_marinov.mygames.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dev_marinov.mygames.data.Games
import com.dev_marinov.mygames.databinding.RvListBinding
import com.squareup.picasso.Picasso

class AdapterList : RecyclerView.Adapter<AdapterList.ViewHolder>(){

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
        val listItemBinding = RvListBinding.inflate(inflater,parent,false)
        return ViewHolder(listItemBinding, mListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(hashMap[position])
    }

    override fun getItemCount() = hashMap.size

    inner class ViewHolder (private val binding: RvListBinding, listener: onItemClickListener)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(games: Games?) {
            binding.itemList = games

            Picasso.get()  // установка главной картинки игры
                .load(games!!.background_image.toString())
                .resize(500, 300) // обязательно свои размеры (т.к. оригинал большой)
                //.placeholder(R.drawable.picture_not_available)
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