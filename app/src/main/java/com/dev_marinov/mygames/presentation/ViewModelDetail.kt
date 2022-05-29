package com.dev_marinov.mygames.presentation

import androidx.lifecycle.ViewModel
import com.dev_marinov.mygames.data.Platform
import com.dev_marinov.mygames.data.Platforms
import com.dev_marinov.mygames.data.Screenshots


class ViewModelDetail : ViewModel(){

    var nameGame = "" // название игры
    var arrayPlatforms: MutableList<Platforms> = ArrayList() // массив платформ
    var released = "" // дата релиза
    var rating = "" // рейтинг текущий
    var ratingTop = "" //тейтинг топ
    var added = "" //сколько людей добавили
    var updated = "" // обновление
    var arrayScreenShots: MutableList<Screenshots> = ArrayList() // скриншоты

}