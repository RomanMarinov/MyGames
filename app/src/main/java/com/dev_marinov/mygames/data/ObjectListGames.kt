package com.dev_marinov.mygames.data

data class ObjectListGames (

    var nameGame: String = "", // название игры
    var arrayPlatforms: ArrayList<String> = ArrayList(),
    var released: String = "",  // дата выхода
    var imgMain: String = "",  // главная картинка
    var rating: String = "",  //рейтинг текущий
    var ratingTop: String = "",  // рейтинг максимальный
    var added: String = "",  // сколько пользователей добавило
    var updated: String = "",  // когда было обновление
    var arrayScreenShots: ArrayList<String> = ArrayList()
)


