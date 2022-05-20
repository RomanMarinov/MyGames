package com.dev_marinov.mygames.presentation

import androidx.lifecycle.ViewModel


class ViewModelDetail : ViewModel(){

    var nameGame = "" // название игры
    var arrayPlatforms: MutableList<String> = ArrayList() // массив платформ
    var released = "" // дата релиза
    var rating = "" // рейтинг текущий
    var ratingTop = "" //тейтинг топ
    var added = "" //сколько людей добавили
    var updated = "" // обновление
    var arrayScreenShots: MutableList<String> = ArrayList() // скриншоты

}