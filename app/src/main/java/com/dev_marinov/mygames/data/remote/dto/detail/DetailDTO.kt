package com.dev_marinov.mygames.data.remote.dto.detail

import com.dev_marinov.mygames.domain.detail.Detail
import com.dev_marinov.mygames.domain.detail.DetailGenres
import com.google.gson.annotations.SerializedName

class DetailDTO (
    @SerializedName("name")
    var name: String?,
    @SerializedName("genres")
    var genres: List<DetailGenresDTO>? = ArrayList(),
    @SerializedName("description")
    var description: String?,
    @SerializedName("updated")
    var updated: String?,
    @SerializedName("background_image")
    var image: String?,
    @SerializedName("rating")
    var rating: String?,
    @SerializedName("rating_top")
    var ratingTop: String?,
    @SerializedName("added")
    var added: String?,
    @SerializedName("released")
    var released: String?,
    @SerializedName("platforms")
    var detailPlatforms: List<DetailPlatformsDTO>? = ArrayList()
) {
    fun mapToDomain(): Detail {
        return Detail(
            name = name,
            genres = genres?.map {
                it.mapToDomain()
            },
            description = description,
            released = released,
            updated = updated,
            image = image,
            rating = rating,
            ratingTop = ratingTop,
            added = added,
            detailPlatforms = detailPlatforms?.map {
                it.platform!!.mapToDomain()
            })
    }
}