package com.dev_marinov.mygames

class ObjectList {

    var nameGame: String? = null

    var arrayPlatforms: MutableList<String>? = null
    var arrayStores: MutableList<String>? = null

    var imgMain: String? = null
    var rating: String? = null
    var ratingTop: String? = null
    var added: String? = null
    var updated: String? = null

    var arrayScreenShots: MutableList<String>? = null
    var arrayGenres: MutableList<String>? = null

    constructor(
        nameGame: String?,
        arrayPlatforms: MutableList<String>?,
        arrayStores: MutableList<String>?,
        imgMain: String?,
        rating: String?,
        ratingTop: String?,
        added: String?,
        updated: String?,
        arrayScreenShots: MutableList<String>?,
        arrayGenres: MutableList<String>?
    ) {
        this.nameGame = nameGame
        this.arrayPlatforms = arrayPlatforms
        this.arrayStores = arrayStores
        this.imgMain = imgMain
        this.rating = rating
        this.ratingTop = ratingTop
        this.added = added
        this.updated = updated
        this.arrayScreenShots = arrayScreenShots
        this.arrayGenres = arrayGenres
    }
}