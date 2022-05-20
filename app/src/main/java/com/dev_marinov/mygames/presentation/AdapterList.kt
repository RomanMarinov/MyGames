package com.dev_marinov.mygames.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.dev_marinov.mygames.data.ObjectListGames
import com.dev_marinov.mygames.R
import com.squareup.picasso.Picasso

class AdapterList() : RecyclerView.Adapter<AdapterList.ViewHolder>(){

    var hashMap: HashMap<Int,ObjectListGames> = HashMap()

    lateinit var mListener: onItemClickListener

    interface onItemClickListener {
        fun onItemClick(position: Int)
    }
    fun setOnItemClickListener(listener: onItemClickListener) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.rv_list, parent, false)
        return ViewHolder(view, mListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val objectList = hashMap[position]

        if (objectList != null) {
                holder.tvNameGame.setText(objectList.nameGame) // установка названия игры
                holder.tvRealised.setText(objectList.released) // установка даты релиза игры

                Picasso.get()  // установка главной картинки игры
                    .load(objectList.imgMain)
                    .resize(500, 300) // обязательно свои размеры (т.к. оригинал большой)
                    //.placeholder(R.drawable.picture_not_available)
                    .centerCrop()
                    .into(holder.myimgMain) // -----> картинка
        }
    }

    override fun getItemCount(): Int {
        return hashMap.size
    }

    //передаем данные и оповещаем адаптер о необходимости обновления списка
    fun refreshListGames(hashMap: HashMap<Int, ObjectListGames>) {
        this.hashMap = hashMap
        notifyDataSetChanged()
    }

    class ViewHolder (itemView: View, listener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {
        val tvNameGame: TextView = itemView.findViewById(R.id.tvNameGame)
        val tvRealised: TextView = itemView.findViewById(R.id.tvRealised)

        val myimgMain: ImageView = itemView.findViewById(R.id.imgMain)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(bindingAdapterPosition)
            }
        }
    }

}