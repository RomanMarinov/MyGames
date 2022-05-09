package com.dev_marinov.mygames

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso

class AdapterList(var requireActivity: FragmentActivity, var hashMap: HashMap<Int, ObjectList>)
    : RecyclerView.Adapter<AdapterList.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterList.ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.rv_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdapterList.ViewHolder, position: Int) {
        val objectList = hashMap[position]

        if (objectList != null) {
                holder.tvNameGame.setText(objectList.nameGame)
                holder.tvRealised.setText(objectList.released)

                Picasso.get()
                    .load(objectList.imgMain)
                    .resize(500, 300)
                    //.placeholder(R.drawable.picture_not_available)
                    .centerCrop()
                    .into(holder.myimgMain) // -----> картинка

        holder.cardView.setOnClickListener {

            val fragmentDetail= FragmentDetail()
            fragmentDetail.getDataDetail(
                hashMap[position]!!.nameGame!!,
                hashMap[position]!!.arrayPlatforms!!,
                hashMap[position]!!.released.toString(),
                hashMap[position]!!.rating.toString(),
                hashMap[position]!!.ratingTop.toString(),
                hashMap[position]!!.added.toString(),
                hashMap[position]!!.updated.toString(),
                hashMap[position]!!.arrayScreenShots!!
            )


            (requireActivity as MainActivity).supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(
                    R.anim.enter_right_to_left,
                    R.anim.exit_right_to_left,
                    R.anim.enter_left_to_right,
                    R.anim.exit_right_to_left)
                .replace(R.id.llFragDetail, fragmentDetail, "llFragDetail")
                .addToBackStack("llFragDetail")
                .commit()


            }

        }

    }

    override fun getItemCount(): Int {
        return hashMap.size
    }

    class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView.findViewById(R.id.cardView)

        val tvNameGame: TextView = itemView.findViewById(R.id.tvNameGame)
        val tvRealised: TextView = itemView.findViewById(R.id.tvRealised)

        val myimgMain: ImageView = itemView.findViewById(R.id.imgMain)
    }

}