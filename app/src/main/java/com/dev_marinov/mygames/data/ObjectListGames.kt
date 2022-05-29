package com.dev_marinov.mygames.data

import com.google.gson.annotations.SerializedName
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList


data class ObjectListGames (val results: List<Games>)


data class Games(
    var name: String?, // название игры
    var released: String?,
    var background_image: String?,
    var rating: String?,
    var rating_top: String?,
    var added: String?,
    var updated: String?,
    var short_screenshots: ArrayList<Screenshots> = ArrayList(),
    var platforms: List<Platforms> = ArrayList()
)

data class Screenshots(
    var image: String?
)


data class Platforms(
    var platform: Platform
)
data class Platform(
    var name: String?

)







//data class ObjectListGames (val results: List<Games>)
//
//data class Games(val name: String?, // название игры
//                 val released: String?,
//                 val background_image: String?,
//                 val rating: String?,
//                 val rating_top: String?,
//                 val added: String?,
//                 val updated: String?,
//                 val short_screenshots: List<Screenshots>,
//                 val platforms: List<Platforms>
//)
//
//data class Screenshots(
//    var image: String?
//)
//
////data class Platforms(
////    var name: String?
////)
//
//data class Platforms(
//    var platform: Platform
//)
//data class Platform(
//    var name: String?
//
//)
