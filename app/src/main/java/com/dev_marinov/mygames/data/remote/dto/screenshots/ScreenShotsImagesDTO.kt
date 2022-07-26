package com.dev_marinov.mygames.data.remote.dto.screenshots

import com.dev_marinov.mygames.domain.screenshot.ScreenShots
import com.dev_marinov.mygames.domain.screenshot.ScreenShotsImages
import com.google.gson.annotations.SerializedName

data class ScreenShotsImagesDTO(
    @SerializedName("image")
    val image: String
) {
    fun mapToDomain() = ScreenShotsImages (image = image)
}
