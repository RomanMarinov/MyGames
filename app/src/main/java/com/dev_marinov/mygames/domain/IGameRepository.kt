package com.dev_marinov.mygames.domain

import com.dev_marinov.mygames.domain.detail.Detail
import com.dev_marinov.mygames.domain.game.Game
import com.dev_marinov.mygames.domain.screenshot.ScreenShots
import com.dev_marinov.mygames.domain.screenshot.ScreenShotsImages

interface IGameRepository {
    suspend fun getGames(apiKey: String, dataFromString: String, dataToString: String, page: Int, platforms: String): List<Game>?

    suspend fun getDetail(apiKey: String, id: String, platforms: String): Detail?

    suspend fun getScreenShot(apiKey: String, id: String, platforms: String): List<ScreenShotsImages>?
}