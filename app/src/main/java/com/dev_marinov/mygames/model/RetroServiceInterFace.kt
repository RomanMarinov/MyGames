package com.dev_marinov.mygames.model

import com.dev_marinov.mygames.data.ObjectListGames
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RetroServiceInterFace {

    //https://api.rawg.io/api/games?key=YOUR_API_KEY&dates=2019-09-01,2019-09-30&platforms=18,1,7

   // "https://api.github.com/search/repositories?q=newyork"

    // передаем параметры
    @GET("games")
    fun getDataFromApi(
        @Query("key")key: String,
        @Query("dates")dates: String,
        @Query("page")page: Int,
        @Query("platforms")platforms: String
    ): Call<ObjectListGames>


//    fun prostoNazvanie(
//        @Query("limit") limit: Int,
//        @Query("offset") offset: Int
//    ): Call<PokemonRequest?>?

}