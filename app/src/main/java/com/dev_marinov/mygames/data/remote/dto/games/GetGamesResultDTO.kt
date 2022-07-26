package com.dev_marinov.mygames.data.remote.dto.games

import com.google.gson.annotations.SerializedName


data class GetGamesResultDTO(
    @SerializedName("results")
    val results: List<GameDTO>
)


