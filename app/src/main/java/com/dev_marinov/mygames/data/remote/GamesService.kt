package com.dev_marinov.mygames.data.remote

import com.dev_marinov.mygames.data.remote.dto.detail.DetailDTO
import com.dev_marinov.mygames.data.remote.dto.games.GetGamesResultDTO
import com.dev_marinov.mygames.data.remote.dto.screenshots.GetResultScreenShotsDTO
import com.dev_marinov.mygames.domain.screenshot.ScreenShots
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GamesService {

    // https://api.rawg.io/api/games/36534/movies?key=e0ecba986417447ebbaa87aad9d31458&platforms=18,1,7


    //https://api.rawg.io/api/games?key=YOUR_API_KEY&dates=2019-09-01,2019-09-30&platforms=18,1,7
    @GET("games")
    suspend fun getGames(
        @Query("key")key: String,
        @Query("dates")dates: String,
        @Query("page")page: Int,
        @Query("platforms")platforms: String
    ): Response<GetGamesResultDTO>

    // https://api.rawg.io/api/games/58617?key=e0ecba986417447ebbaa87aad9d31458&platforms=18,1,7
    @GET("games/{id}")
    suspend fun getDetail(
        @Path("id")id: String,
        @Query("key")key: String,
        @Query("platforms")platforms: String
    ): Response<DetailDTO>

    // https://api.rawg.io/api/games/58617/screenshots?key=e0ecba986417447ebbaa87aad9d31458&platforms=18,1,7
    @GET("games/{id}/screenshots")
    suspend fun getScreenShot(
        @Path("id")id: String,
        @Query("key")key: String,
        @Query("platforms")platforms: String
    ): Response<GetResultScreenShotsDTO>
}