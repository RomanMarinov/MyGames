package com.dev_marinov.mygames.data

import com.dev_marinov.mygames.domain.Platforms
import com.dev_marinov.mygames.domain.Screenshot

data class ObjectListDetail(
    var nameGame: String,
    var released: String,
    var rating: String,
    var ratingTop: String,
    var added: String,
    var updated: String,
    var arrayScreenShots: MutableList<Screenshot> = ArrayList(),
    var arrayPlatforms: MutableList<Platforms> = ArrayList()
)