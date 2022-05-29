package com.dev_marinov.mygames.presentation

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.dev_marinov.mygames.data.Games
import com.dev_marinov.mygames.data.ObjectListGames
import com.dev_marinov.mygames.data.*
import com.dev_marinov.mygames.model.MyApplication
import com.dev_marinov.mygames.model.RetroServiceInterFace
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject


class ViewModelListGames(application: Application) : AndroidViewModel(application) {

    //private var hashMapGames: MutableLiveData<HashMap<Int, ObjectListGames>> = MutableLiveData()

    @Inject
    lateinit var retroServiceInterFace: RetroServiceInterFace

    private var hashMapGames: MutableLiveData<HashMap<Int, Games>>
    val hashMap: HashMap<Int, Games>

    //инициализируем список и заполняем его данными пользователей
    init {
        // тут мы инициализируем наш класс приложения
        (application as MyApplication).getRetroComponent().inject(this)

        hashMap = HashMap()

        hashMapGames = MutableLiveData()

    }

    fun getHashMapGames(): MutableLiveData<HashMap<Int, Games>> {
        return hashMapGames
    }

    // https://api.rawg.io/api/games?key=YOUR_API_KEY&dates=2019-09-01,2019-09-30&platforms=18,1,7

    // функция которая будет вызвать апи
    fun makeApicall(apiKey: String, dataFromString: String, dataToString: String, page: Int, viewModelFlagLoading: ViewModelFlagLoading) {

        val call: Call<ObjectListGames> = retroServiceInterFace.getDataFromApi(
            apiKey,
            dataFromString + dataToString,
            page,
            "18,1,7")
        call.enqueue(object : Callback<ObjectListGames> {
            override fun onResponse(call: Call<ObjectListGames>, response: Response<ObjectListGames>) {
                Log.e("333","=onResponse=" + response.message())

                if(response.isSuccessful) {
                    val items = response.body()
                    if (items != null) {
                            Log.e("333", "items.results.indices="+items.results.indices)
                        for (i in items.results.indices) {

                            val name = items.results[i].name
                            val released = items.results[i].released
                            val background_image = items.results[i].background_image
                            val rating = items.results[i].rating
                            val rating_top = items.results[i].rating_top
                            val added = items.results[i].added
                            val updated = items.results[i].updated
                            val short_screenshots = items.results[i].short_screenshots
                            val platforms = items.results[i].platforms

                            hashMap[hashMap.size] = Games(
                                name,
                                released,
                                background_image,
                                rating,
                                rating_top,
                                added,
                                updated,
                                short_screenshots,
                                platforms
                            )
                        }
                    // setValue уместен в основном потоке приложения, а postValue — если данные приходят из фонового потока.
                        hashMapGames.postValue(hashMap)
                    } else {
                    hashMapGames.postValue(null)
                    }
                }
            }
            override fun onFailure(call: Call<ObjectListGames>, t: Throwable) {
                Log.e("333","=onFailure=" + t)
                // setValue уместен в основном потоке приложения, а postValue — если данные приходят из фонового потока.
                hashMapGames.postValue(null)
            }
        })
    }

}