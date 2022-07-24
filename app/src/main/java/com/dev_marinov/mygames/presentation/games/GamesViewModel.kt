package com.dev_marinov.mygames.presentation.games

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dev_marinov.mygames.MyApplication
import com.dev_marinov.mygames.data.remote.GamesService
import com.dev_marinov.mygames.domain.Game
import com.dev_marinov.mygames.domain.GetGamesResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject


class GamesViewModel(application: Application) : AndroidViewModel(application){

    private val apiKey = "e0ecba986417447ebbaa87aad9d31458"
    private val platform = "18,1,7"

    var flagLoading = false
    var dataFromString = "2019-09-01,"
    var dataToString = "2019-09-30"
    var lastVisibleItemPosition = 0
    var page = 1

    var statusAlertDialogDate = false
    var statusLeftDate = false
    var statusRightDate = false

    var totalCountItem = 0

    @Inject
    lateinit var retroServiceInterFace: GamesService

    private var _hashMapGames: MutableLiveData<HashMap<Int, Game>> = MutableLiveData()
    var hashMapGames: LiveData<HashMap<Int, Game>> = _hashMapGames
    val hashMapTemp: HashMap<Int, Game> = HashMap()

    //инициализируем список и заполняем его данными пользователей
    init {
        // тут мы инициализируем наш класс приложения
        (application as MyApplication).getRetroComponent().inject(this)
        getGames(apiKey,dataFromString,dataToString,page)
    }

    // https://api.rawg.io/api/games?key=YOUR_API_KEY&dates=2019-09-01,2019-09-30&platforms=18,1,7

    fun getGames(apiKey: String, dataFromString: String, dataToString: String, page: Int) {
        Log.e("333","=getGames=")

        val call: Call<GetGamesResult> = retroServiceInterFace.getDataFromApi(
            key = apiKey,
            dates = dataFromString + dataToString,
            page = page,
            platforms = platform)
        call.enqueue(object : Callback<GetGamesResult> {
            override fun onResponse(call: Call<GetGamesResult>, response: Response<GetGamesResult>) {
                Log.e("333","=onResponse=")

                if(response.isSuccessful) {
                    val items = response.body()
                    if (items != null) {
                            Log.e("333", "items.results.indices="+items.results.indices)
                        for (i in items.results.indices) {

                            val name = items.results[i].name
                            val released = items.results[i].released
                            val backgroundImage = items.results[i].background_image
                            val rating = items.results[i].rating
                            val ratingTop = items.results[i].rating_top
                            val added = items.results[i].added
                            val updated = items.results[i].updated
                            val shortScreenshots = items.results[i].short_screenshots
                            val platforms = items.results[i].platforms

                            hashMapTemp[hashMapTemp.size] = Game(
                                name,
                                released,
                                backgroundImage,
                                rating,
                                ratingTop,
                                added,
                                updated,
                                shortScreenshots,
                                platforms
                            )
                        }
                    // setValue уместен в основном потоке приложения, а postValue — если данные приходят из фонового потока.
                        _hashMapGames.postValue(hashMapTemp)
                    }
                }
            }
            override fun onFailure(call: Call<GetGamesResult>, t: Throwable) {
                Log.e("333","=onFailure=" + t)
            }
        })
    }

}