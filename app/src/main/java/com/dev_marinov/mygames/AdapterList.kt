package com.dev_marinov.mygames

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso

class AdapterList(requireActivity: FragmentActivity, var hashMap: HashMap<Int, ObjectList>)
    : RecyclerView.Adapter<AdapterList.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterList.ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.rv_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdapterList.ViewHolder, position: Int) {
        val objectList = hashMap[position]

        if (objectList != null) {
            Picasso.get()
                .load(objectList.imgMain).memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(holder.myimgMain) // -----> картинка
        }

    }

    override fun getItemCount(): Int {
        return hashMap.size
    }

    class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        val myimgMain: ImageView = itemView.findViewById(R.id.imgMain)
    }
}