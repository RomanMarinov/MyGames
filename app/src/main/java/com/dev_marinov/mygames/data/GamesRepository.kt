package com.dev_marinov.mygames.data

import android.util.Log
import com.dev_marinov.mygames.data.remote.GamesService
import com.dev_marinov.mygames.domain.game.Game
import com.dev_marinov.mygames.domain.IGameRepository
import com.dev_marinov.mygames.domain.detail.Detail
import com.dev_marinov.mygames.domain.screenshot.ScreenShotsImages
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GamesRepository @Inject constructor(private val gamesService: GamesService) : IGameRepository {

    override suspend fun getGames(dataFromString: String, dataToString: String, page: Int
    ): List<Game>? {
        val response = gamesService.getGames(dates = dataFromString + dataToString, page = page)
        val game = response.body()?.let { getGamesResultDTO ->
            getGamesResultDTO.results.map {
                it.mapToDomain()
            }
        }
        return game
    }

    override suspend fun getDetail(id: String): Detail? {
        val response = gamesService.getDetail(id = id)
        Log.e("333", "getDetail response=" + response)
        val detail = response.body()?.let { getDetailResultDTO ->
            getDetailResultDTO.mapToDomain()
        }
        return detail
    }

    override suspend fun getScreenShot(id: String): List<ScreenShotsImages>? {
        val response = gamesService.getScreenShot(id = id)
        val screenShots = response.body()?.let {
            it.results.map { screenShotsImagesDTO ->
                screenShotsImagesDTO.mapToDomain()
            }
        }
            return screenShots
    }
}