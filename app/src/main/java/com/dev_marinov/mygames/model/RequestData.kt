package com.dev_marinov.mygames.model

import android.util.Log
import com.dev_marinov.mygames.data.ObjectListGames
import com.dev_marinov.mygames.presentation.MainActivity
import com.dev_marinov.mygames.presentation.ViewModelFlagLoading
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.RequestParams
import com.loopj.android.http.TextHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONException
import org.json.JSONObject

object RequestData {

    var hashMap: HashMap<Int, ObjectListGames> = HashMap()

    // метод получения данных
    fun getData(
        dataFrom: String,
        dataTo: String,
        page: Int,
        viewModelFlagLoading: ViewModelFlagLoading
    ) {

// https://api.rawg.io/api/games?key=YOUR_API_KEY&dates=2019-09-01,2019-09-30&platforms=18,1,7
        val asyncHttpClient = AsyncHttpClient()

        val requestParams = RequestParams()
        requestParams.put("key", "e0ecba986417447ebbaa87aad9d31458")
        requestParams.put("dates", String.format(dataFrom + dataTo))
        requestParams.put("page", page)
        requestParams.put("platforms", "18,1,7")

        asyncHttpClient.get("https://api.rawg.io/api/games", requestParams, object : TextHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseString: String?) {
                Log.e("333", "-onSuccess-" + responseString)

                try {
                    val jsonObject = JSONObject(responseString)
                    val jsonArray = jsonObject.getJSONArray("results")

                    for (n in 0 until jsonArray.length()) {
                        val nameGame = jsonObject.getJSONArray("results").getJSONObject(n).getString("name")//-----

                        val arrayPlatforms: MutableList<String> = ArrayList() // массив для записи платформ у игры //------
                        // по platforms считаю от 0 до ... платформы игры
                        val platforms = jsonObject.getJSONArray("results").getJSONObject(n).getJSONArray("platforms")
                        for (z in 0 until platforms.length()) {
                            val platform = jsonObject.getJSONArray("results").getJSONObject(n).getJSONArray("platforms")
                                .getJSONObject(z).getJSONObject("platform").getString("name")
                            arrayPlatforms.add(z, platform)
                        }

                        val released = jsonObject.getJSONArray("results").getJSONObject(n).getString("released") //------
                        val imgMain = jsonObject.getJSONArray("results").getJSONObject(n).getString("background_image") //------

                        val rating = jsonObject.getJSONArray("results").getJSONObject(n).getString("rating") //------
                        val ratingTop = jsonObject.getJSONArray("results").getJSONObject(n).getString("rating_top") //------

                        val added = jsonObject.getJSONArray("results").getJSONObject(n).getString("added") //------
                        val updated = jsonObject.getJSONArray("results").getJSONObject(n).getString("updated") //------

                        val arrayListScreenShots: MutableList<String> = ArrayList()
                        val screenshots = jsonObject.getJSONArray("results").getJSONObject(n).getJSONArray("short_screenshots")
                        for (y in 0 until screenshots.length()) {
                            val screenshot = jsonObject.getJSONArray("results").getJSONObject(n).getJSONArray("short_screenshots")
                                .getJSONObject(y).getString("image")
                            arrayListScreenShots.add(y, screenshot)
                        }

                        hashMap.set(hashMap.size, ObjectListGames(
                            nameGame,
                            arrayPlatforms as ArrayList<String>,
                            released,
                            imgMain,
                            rating,
                            ratingTop,
                            added,
                            updated,
                            arrayListScreenShots as ArrayList<String>
                        )
                        )

                    }

                    // интрерфейс срабатывает когда массив наполнился
                    MainActivity.myInterFaceGames.methodMyInterFaceGames()

                    viewModelFlagLoading.flagLoading = false
                }
                catch (e: JSONException) {
                    Log.e("333", "-try catch=" + e)
                }
            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseString: String?, throwable: Throwable?) {
                Log.e("333", "-onFailure-" + responseString)
            }
        })
    }
    fun getHashMapGames() = hashMap
}