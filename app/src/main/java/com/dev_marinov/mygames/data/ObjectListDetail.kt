package com.dev_marinov.mygames.data

data class ObjectListDetail(
    var nameGame: String = "", // название игры
    var released: String = "",  // дата выхода
    var rating: String = "",  //рейтинг текущий
    var ratingTop: String = "",  // рейтинг максимальный
    var added: String = "",  // сколько пользователей добавило
    var updated: String = "",  // когда было обновление
    var arrayScreenShots: MutableList<Screenshots> = ArrayList(),
    var arrayPlatforms: MutableList<Platforms> = ArrayList()
)