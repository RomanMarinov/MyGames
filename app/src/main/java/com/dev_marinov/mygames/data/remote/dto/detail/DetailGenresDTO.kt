package com.dev_marinov.mygames.data.remote.dto.detail

import com.dev_marinov.mygames.domain.detail.Detail
import com.dev_marinov.mygames.domain.detail.DetailGenres
import com.google.gson.annotations.SerializedName

data class DetailGenresDTO(
    @SerializedName("name")
    val name: String?
) {
    fun mapToDomain() = DetailGenres(name = name)
}
