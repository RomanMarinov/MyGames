package com.dev_marinov.mygames.domain

data class Game(
    var name: String?,
    var released: String?,
    var background_image: String?,
    var rating: String?,
    var rating_top: String?,
    var added: String?,
    var updated: String?,
    var short_screenshots: ArrayList<Screenshot> = ArrayList(),
    var platforms: List<Platform> = ArrayList()
)
