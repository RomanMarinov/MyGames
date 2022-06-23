package com.dev_marinov.mygames.presentation.detail

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dev_marinov.mygames.R
import com.dev_marinov.mygames.data.ObjectListDetail
import com.dev_marinov.mygames.databinding.FragmentDetailBinding
import kotlin.collections.HashMap

class DetailFragment : Fragment() {

    private lateinit var bindingFragmentDetail: FragmentDetailBinding

    lateinit var sharedViewModel: SharedViewModel
    lateinit var viewModelDetail: ViewModelDetail

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        bindingFragmentDetail = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container,false)

        viewModelDetail = ViewModelProvider(this)[ViewModelDetail::class.java]
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        sharedViewModel.message.observe(viewLifecycleOwner) {
            viewModelDetail.detail = it
            setDetail()
        }

        return bindingFragmentDetail.root;
    }

    private fun setDetail() {

        bindingFragmentDetail.tvName.text = viewModelDetail.detail[0]!!.nameGame
        bindingFragmentDetail.tvReleased.text = viewModelDetail.detail[0]!!.released
        bindingFragmentDetail.tvRating.text = viewModelDetail.detail[0]!!.rating
        bindingFragmentDetail.tvRatingTop.text = viewModelDetail.detail[0]!!.ratingTop
        bindingFragmentDetail.tvAdded.text = viewModelDetail.detail[0]!!.added
        bindingFragmentDetail.tvUpdated.text = viewModelDetail.detail[0]!!.updated.substring(0, 10) // урезать дату

        // установка названий платфом через перебор arrayPlatforms
        for (item in viewModelDetail.detail[0]!!.arrayPlatforms.indices) {
            bindingFragmentDetail.tvArrayPlatforms.text = (bindingFragmentDetail.tvArrayPlatforms.text.toString()
                    + (viewModelDetail.detail[0]!!.arrayPlatforms[item].platform.name.toString()) + ", ")
        }

        val linearLayoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        val gamesDetailAdapter = GamesDetailAdapter(viewModelDetail.detail[0]!!.arrayScreenShots)

        bindingFragmentDetail.recyclerViewDetail.apply {
            layoutManager = linearLayoutManager
            adapter = gamesDetailAdapter
        }
    }

}