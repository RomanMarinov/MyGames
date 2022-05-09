package com.dev_marinov.mygames

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FragmentDetail : Fragment() {

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

    var newNameGame: String? = null
    var newReleased: String? = null
    var newRating: String? = null
    var newRatingTop: String? = null
    var newAdded: String? = null
    var newUpdated: String? = null
    var newArrayScreenShots: MutableList<String>? = ArrayList()
    var newArrayPlatforms: MutableList<String> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val view: View

        view = layoutInflater.inflate(R.layout.fragment_detail, container, false)

        tvName = view.findViewById(R.id.tvName)
        tvReleased = view.findViewById(R.id.tvReleased)
        tvRating = view.findViewById(R.id.tvRating)
        tvRatingTop = view.findViewById(R.id.tvRatingTop)
        tvAdded = view.findViewById(R.id.tvAdded)
        tvUpdated = view.findViewById(R.id.tvUpdated)
        tvArrayPlatforms = view.findViewById(R.id.tvArrayPlatforms)

        tvName.text = newNameGame
        tvReleased.text = newReleased
        tvRating.text = newRating
        tvRatingTop.text = newRatingTop
        tvAdded.text = newAdded
        tvUpdated.text = newUpdated?.substring(0, 10) // урезать дату

            // удаление из стрингов лишние символы для вывода пользователю
        val string3 = newArrayPlatforms.toString().replace("[","")
        val string4 = string3.toString().replace("]","")
        tvArrayPlatforms.text = string4.toString()

        recyclerViewDetail = view.findViewById(R.id.recyclerViewDetail)

        linearLayoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        recyclerViewDetail.layoutManager = linearLayoutManager

        adapterListDetail = AdapterListDetail(newArrayScreenShots)
        recyclerViewDetail.adapter = adapterListDetail

        return view;
    }

    // метод установки данных во views
    fun getDataDetail(nameGame: String, // название игры
                      arrayPlatforms: MutableList<String>, // массив платформ
                      released: String, // дата релиза
                      rating: String, // рейтинг текущий
                      ratingTop: String, //тейтинг топ
                      added: String, //сколько людей добавили
                      updated: String, // обновление
                      arrayScreenShots: MutableList<String> // скриншоты
    ) { // жанры
        newNameGame = nameGame
        newArrayScreenShots = arrayScreenShots
        newArrayPlatforms = arrayPlatforms
        newReleased = released
        newRating = rating
        newRatingTop = ratingTop
        newAdded = added
        newUpdated = updated
    }

}