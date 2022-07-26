package com.dev_marinov.mygames.data.remote.dto.detail

import com.dev_marinov.mygames.domain.detail.DetailPlatform
import com.google.gson.annotations.SerializedName

data class DetailPlatformDTO(
    @SerializedName("name")
    val name: String?
) {
    fun mapToDomain() = DetailPlatform(name = name)
}
