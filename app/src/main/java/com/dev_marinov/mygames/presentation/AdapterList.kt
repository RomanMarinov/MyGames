package com.dev_marinov.mygames.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dev_marinov.mygames.data.Games
import com.dev_marinov.mygames.R
import com.dev_marinov.mygames.data.ObjectListGames
import com.squareup.picasso.Picasso

class AdapterList : RecyclerView.Adapter<AdapterList.ViewHolder>(){

    var hashMap: HashMap<Int, Games> = HashMap()

//    private var listData: List<Games>? = null

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
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.rv_list, parent, false)
        return ViewHolder(view, mListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val objectList = hashMap[position]
//
//        if (objectList != null) {

                holder.tvNameGame.text = hashMap[position]!!.name
                holder.tvNameGame.text = hashMap[position]!!.released
                Picasso.get()  // установка главной картинки игры
                    .load(hashMap[position]!!.background_image)
                    .resize(500, 300) // обязательно свои размеры (т.к. оригинал большой)
                    //.placeholder(R.drawable.picture_not_available)
                    .centerCrop()
                    .into(holder.myimgMain) // -----> картинка


//                holder.tvNameGame.text = hashMap[position].results.toString()
//
//                holder.tvNameGame.setText(listData!![position].name) // установка названия игры
//                holder.tvRealised.setText(listData!![position].released) // установка даты релиза игры

//                Picasso.get()  // установка главной картинки игры
//                    .load(listData!![position].background_image)
//                    .resize(500, 300) // обязательно свои размеры (т.к. оригинал большой)
//                    //.placeholder(R.drawable.picture_not_available)
//                    .centerCrop()
//                    .into(holder.myimgMain) // -----> картинка
  //      }
    }

//    override fun getItemCount(): Int {
//        return hashMap.size
//    }

    override fun getItemCount() = if (hashMap == null) 0 else hashMap!!.size


//    //передаем данные и оповещаем адаптер о необходимости обновления списка
//    fun refreshListGames(hashMap: HashMap<Int, ObjectListGames>) {
//        this.hashMap = hashMap
//        notifyDataSetChanged()
//    }

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