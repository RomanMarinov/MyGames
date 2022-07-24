package com.dev_marinov.mygames.domain


data class GetGamesResult(
    val results: List<Game>
)

data class Platforms(
    var platform: Platform
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
