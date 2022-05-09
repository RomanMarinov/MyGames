package com.dev_marinov.mygames

class ObjectList {

    var nameGame: String? = null // название игры

    var arrayPlatforms: MutableList<String>? = null

    var released: String? = null // дата выхода
    var imgMain: String? = null // главная картинка
    var rating: String? = null //рейтинг текущий
    var ratingTop: String? = null // рейтинг максимальный
    var added: String? = null // сколько пользователей добавило
    var updated: String? = null // когда было обновление

    var arrayScreenShots: MutableList<String>? = null

    constructor(
        nameGame: String?,
        arrayPlatforms: MutableList<String>?,
        released: String?,
        imgMain: String?,
        rating: String?,
        ratingTop: String?,
        added: String?,
        updated: String?,
        arrayScreenShots: MutableList<String>?
    ) {
        this.nameGame = nameGame
        this.arrayPlatforms = arrayPlatforms
        this.released = released
        this.imgMain = imgMain
        this.rating = rating
        this.ratingTop = ratingTop
        this.added = added
        this.updated = updated
        this.arrayScreenShots = arrayScreenShots
    }
}