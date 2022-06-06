package com.dev_marinov.mygames.presentation

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev_marinov.mygames.DataBinderMapperImpl
import com.dev_marinov.mygames.R
import com.dev_marinov.mygames.data.ObjectListDetail
import com.dev_marinov.mygames.databinding.FragmentDetailBinding
import kotlin.collections.HashMap

class FragmentDetail : Fragment() {

    private lateinit var bindingFragmentDetail: FragmentDetailBinding

    lateinit var model: SharedViewModel
    lateinit var viewModelDetail: ViewModelDetail

    var adapterListDetail: AdapterListDetail? = null
    var linearLayoutManager: LinearLayoutManager? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        bindingFragmentDetail = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container,false)

        viewModelDetail = ViewModelProvider(this)[ViewModelDetail::class.java]

        model = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        model.message.observe(viewLifecycleOwner, Observer {
            setDetail(it)
        })

        return bindingFragmentDetail.root;
    }

    private fun setDetail(hashMap: HashMap<Int, ObjectListDetail>) {
        // записываем в новый viewModelDetail
        viewModelDetail.nameGame = hashMap[0]!!.nameGame
        viewModelDetail.arrayPlatforms = hashMap[0]!!.arrayPlatforms
        viewModelDetail.released = hashMap[0]!!.released
        viewModelDetail.rating = hashMap[0]!!.rating
        viewModelDetail.ratingTop = hashMap[0]!!.ratingTop
        viewModelDetail.added = hashMap[0]!!.added
        viewModelDetail.updated = hashMap[0]!!.updated
        viewModelDetail.arrayScreenShots = hashMap[0]!!.arrayScreenShots

        bindingFragmentDetail.tvName.text = viewModelDetail.nameGame
        bindingFragmentDetail.tvReleased.text = viewModelDetail.released
        bindingFragmentDetail.tvRating.text = viewModelDetail.rating
        bindingFragmentDetail.tvRatingTop.text = viewModelDetail.ratingTop
        bindingFragmentDetail.tvAdded.text = viewModelDetail.added
        bindingFragmentDetail.tvUpdated.text = viewModelDetail.updated.substring(0, 10) // урезать дату

        // установка названий платфом через перебор arrayPlatforms
        for (item in viewModelDetail.arrayPlatforms.indices) {
            bindingFragmentDetail.tvArrayPlatforms.text = (bindingFragmentDetail.tvArrayPlatforms.text.toString()
                    + (viewModelDetail.arrayPlatforms[item].platform.name.toString()) + ", ")
        }

        linearLayoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)

        adapterListDetail = AdapterListDetail(viewModelDetail.arrayScreenShots)

        bindingFragmentDetail.recyclerViewDetail.apply {
            layoutManager = linearLayoutManager
            adapter = adapterListDetail
        }
    }

}