package com.dev_marinov.mygames.presentation

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev_marinov.mygames.R
import com.dev_marinov.mygames.data.ObjectListDetail


class FragmentDetail : Fragment() {

    lateinit var model: SharedViewModel
    lateinit var viewModelDetail: ViewModelDetail

    lateinit var recyclerViewDetail: RecyclerView
    var adapterListDetail: AdapterListDetail? = null
    var linearLayoutManager: LinearLayoutManager? = null

    lateinit var tvName: TextView
    lateinit var tvReleased: TextView
    lateinit var tvRating: TextView
    lateinit var tvRatingTop: TextView
    lateinit var tvAdded: TextView
    lateinit var tvUpdated: TextView
    lateinit var tvArrayPlatforms: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view: View

        view = layoutInflater.inflate(R.layout.fragment_detail, container, false)

        Log.e("333","=зашел в FragmentDetail=")

        viewModelDetail = ViewModelProvider(this).get(ViewModelDetail::class.java)

        tvName = view.findViewById(R.id.tvName)
        tvReleased = view.findViewById(R.id.tvReleased)
        tvRating = view.findViewById(R.id.tvRating)
        tvRatingTop = view.findViewById(R.id.tvRatingTop)
        tvAdded = view.findViewById(R.id.tvAdded)
        tvUpdated = view.findViewById(R.id.tvUpdated)
        tvArrayPlatforms = view.findViewById(R.id.tvArrayPlatforms)

        model = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        model.message.observe(viewLifecycleOwner, Observer {
            setDetail(it, view)
        })

        return view;
    }


    fun setDetail(hashMap: HashMap<Int, ObjectListDetail>, view: View) {
            viewModelDetail.nameGame = hashMap[0]!!.nameGame
            viewModelDetail.arrayPlatforms = hashMap[0]!!.arrayPlatforms
            viewModelDetail.released = hashMap[0]!!.released
            viewModelDetail.rating = hashMap[0]!!.rating
            viewModelDetail.ratingTop = hashMap[0]!!.ratingTop
            viewModelDetail.added = hashMap[0]!!.added
            viewModelDetail.updated = hashMap[0]!!.updated
            viewModelDetail.arrayScreenShots = hashMap[0]!!.arrayScreenShots

            tvName.text = viewModelDetail.nameGame
            tvReleased.text = viewModelDetail.released
            tvRating.text = viewModelDetail.rating
            tvRatingTop.text = viewModelDetail.ratingTop
            tvAdded.text = viewModelDetail.added
            //tvUpdated.text = viewModelDetail.updated.substring(0, 10) // урезать дату

            // удаление из стрингов лишние символы для вывода пользователю
            val string3 = viewModelDetail.arrayPlatforms.toString().replace("[","")
            val string4 = string3.toString().replace("]","")
            tvArrayPlatforms.text = string4.toString()

            recyclerViewDetail = requireView().findViewById(R.id.recyclerViewDetail)

            linearLayoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
            recyclerViewDetail.layoutManager = linearLayoutManager

            adapterListDetail = AdapterListDetail(viewModelDetail.arrayScreenShots)
            recyclerViewDetail.adapter = adapterListDetail
    }

}