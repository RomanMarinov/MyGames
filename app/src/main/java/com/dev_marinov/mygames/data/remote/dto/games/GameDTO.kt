package com.dev_marinov.mygames.data.remote.dto.games

import com.dev_marinov.mygames.domain.game.Game
import com.google.gson.annotations.SerializedName

class GameDTO (
    @SerializedName("name")
    val name: String?,
    @SerializedName("released")
    val released: String?,
    @SerializedName("background_image")
    val image: String?,
    @SerializedName("id")
    val id: String
) {
    fun mapToDomain() : Game {
        return Game(
            name = name,
            released = released,
            image = image,
            id = id
        )
    }
}


