package com.dev_marinov.mygames.presentation.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.dev_marinov.mygames.R
import com.dev_marinov.mygames.databinding.FragmentDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailFragment : Fragment() {

    private lateinit var binding: FragmentDetailBinding
    private val args: DetailFragmentArgs by navArgs()

    private val viewModel by viewModels<DetailViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container, false)
        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setLayout()
    }

    private fun setLayout() {

        val linearLayoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)

        val adapter = DetailAdapter()
        binding.recyclerViewDetail.apply {
            layoutManager = linearLayoutManager
            this.adapter = adapter
        }

        viewModel.getDetail(args.id)

        viewModel.detail.observe(viewLifecycleOwner) {

            binding.tvName.text = it.name
            binding.tvReleased.text = it.released
            binding.tvRating.text = it.rating
            binding.tvRatingTop.text = it.ratingTop
            binding.tvAdded.text = it.added
            binding.tvUpdated.text = it.updated?.substring(0, 10) // урезать дату

            it.description?.let { string->
                val text1 = string.replace("<p>","")
                val text2 = text1.replace("<br />", "")
                val text3 = text2.replace("</p>","")
                binding.tvDescription.text = text3
            }

            for (item in it.genres!!.indices) {
                binding.tvGenres.text = (binding.tvGenres.text.toString()
                        + (it.genres[item].name.toString()) + ", ")
                if (item == it.genres.size - 1) {
                    binding.tvGenres.text = binding.tvGenres.text.substring(0, binding.tvGenres.text.length - 2)
                }
            }

            for (item in it.detailPlatforms!!.indices) {
                binding.tvPlatforms.text = (binding.tvPlatforms.text.toString()
                        + (it.detailPlatforms[item].name.toString()) + ", ")
                if (item == it.detailPlatforms.size - 1) {
                    binding.tvPlatforms.text = binding.tvPlatforms.text.substring(0, binding.tvPlatforms.text.length - 2)
                }
            }
        }

        viewModel.screenShots.observe(viewLifecycleOwner){
            adapter.submitList(it)
        }
    }

}