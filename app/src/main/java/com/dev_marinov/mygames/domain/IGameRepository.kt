package com.dev_marinov.mygames.domain

import com.dev_marinov.mygames.domain.detail.Detail
import com.dev_marinov.mygames.domain.game.Game
import com.dev_marinov.mygames.domain.screenshot.ScreenShots
import com.dev_marinov.mygames.domain.screenshot.ScreenShotsImages

interface IGameRepository {

    suspend fun getGames(dataFromString: String, dataToString: String, page: Int): List<Game>?

    suspend fun getDetail(id: String): Detail?

    suspend fun getScreenShot(id: String): List<ScreenShotsImages>?
}