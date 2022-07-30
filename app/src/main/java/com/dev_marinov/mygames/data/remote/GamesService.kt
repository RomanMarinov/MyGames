package com.dev_marinov.mygames.data.remote

import com.dev_marinov.mygames.data.remote.dto.detail.DetailDTO
import com.dev_marinov.mygames.data.remote.dto.games.GetGamesResultDTO
import com.dev_marinov.mygames.data.remote.dto.screenshots.GetResultScreenShotsDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

    private val default_key: String = "e0ecba986417447ebbaa87aad9d31458"
    private val default_platforms: String = "18,1,7"

interface GamesService {

    //https://api.rawg.io/api/games?key=YOUR_API_KEY&dates=2019-09-01,2019-09-30&platforms=18,1,7
    @GET("games")
    suspend fun getGames(
        @Query("key")key: String = default_key,
        @Query("dates")dates: String,
        @Query("page")page: Int,
        @Query("platforms")platforms: String = default_platforms
    ): Response<GetGamesResultDTO>

    // https://api.rawg.io/api/games/58617?key=e0ecba986417447ebbaa87aad9d31458&platforms=18,1,7
    @GET("games/{id}")
    suspend fun getDetail(
        @Path("id")id: String,
        @Query("key")key: String = default_key,
        @Query("platforms")platforms: String = default_platforms
    ): Response<DetailDTO>

    // https://api.rawg.io/api/games/58617/screenshots?key=e0ecba986417447ebbaa87aad9d31458&platforms=18,1,7
    @GET("games/{id}/screenshots")
    suspend fun getScreenShot(
        @Path("id")id: String,
        @Query("key")key: String = default_key,
        @Query("platforms")platforms: String = default_platforms
    ): Response<GetResultScreenShotsDTO>
}