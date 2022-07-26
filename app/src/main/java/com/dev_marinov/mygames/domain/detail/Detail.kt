package com.dev_marinov.mygames.domain.detail

data class Detail(
    val name: String?,
    val genres: List<DetailGenres>?,
    val description: String?,
    val released: String?,
    val updated: String?,
    val image: String?,
    val rating: String?,
    val ratingTop: String?,
    val added: String?,
    val detailPlatforms: List<DetailPlatform>? = ArrayList()
)
