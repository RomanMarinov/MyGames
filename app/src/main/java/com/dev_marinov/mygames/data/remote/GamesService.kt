package com.dev_marinov.mygames.data.remote

import com.dev_marinov.mygames.domain.GetGamesResult
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GamesService {

    //https://api.rawg.io/api/games?key=YOUR_API_KEY&dates=2019-09-01,2019-09-30&platforms=18,1,7

   // "https://api.github.com/search/repositories?q=newyork"

    // передаем параметры
    @GET("games")
    fun getDataFromApi(
        @Query("key")key: String,
        @Query("dates")dates: String,
        @Query("page")page: Int,
        @Query("platforms")platforms: String
    ): Call<GetGamesResult>

}