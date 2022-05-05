package com.dev_marinov.mygames

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.RequestParams
import com.loopj.android.http.TextHttpResponseHandler
import cz.msebera.android.httpclient.Header
import okhttp3.internal.platform.Platform
import org.json.JSONException
import org.json.JSONObject

class FragmentList : Fragment() {

    // api =e0ecba986417447ebbaa87aad9d31458
    lateinit var recyclerView: RecyclerView
    var adapterList: AdapterList? = null
    var myViewGroup: ViewGroup? = null
    var myLayoutInflater: LayoutInflater? = null
    var staggeredGridLayoutManager: StaggeredGridLayoutManager? = null

    lateinit var tv: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        myViewGroup = container
        myLayoutInflater = inflater

        return initInterface()
    }

    fun initInterface () : View {
        val view: View

        if (myViewGroup != null) { // при пересоздании макета, если он не пуст, он будет очищен
            myViewGroup?.removeAllViewsInLayout()
        }
        // получить int ориентации
        val orientation = requireActivity().resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            view = layoutInflater.inflate(R.layout.fragment_list, myViewGroup, false)

            myRecyclerLayoutManagerAdapter(view, 2, (activity as MainActivity?)?.lastVisibleItem)
        } else {
            view = layoutInflater.inflate(R.layout.fragment_list, myViewGroup, false)

            myRecyclerLayoutManagerAdapter(view, 2, (activity as MainActivity?)?.lastVisibleItem)
        }

        if ((activity as MainActivity?)?.hashMap?.size == 0) {
            getData()
        } else {
            Log.e("333", "FragmentHome arrayList.size()  НЕ ПУСТОЙ=")
        }

        return view
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        // ДО СОЗДАНИЯ НОВОГО МАКЕТА ПИШЕМ ПЕРЕМЕННЫЕ В КОТОРЫЕ СОХРАНЯЕМ ЛЮБЫЕ ДАННЫЕ ИЗ ТЕКУЩИХ VIEW
        // создать новый макет------------------------------
        val view: View = initInterface()
        // ПОСЛЕ СОЗДАНИЯ НОВОГО МАКЕТА ПЕРЕДАЕМ СОХРАНЕННЫЕ ДАННЫЕ В СТАРЫЕ(ТЕ КОТОРЫЕ ТЕКУЩИЕ) VIEW
        // отображать новую раскладку на экране
        myViewGroup?.addView(view)
        super.onConfigurationChanged(newConfig)
    }

    // метод для установки recyclerview, GridLayoutManager и AdapterListHome
    fun myRecyclerLayoutManagerAdapter(view: View, column: Int, lastVisableItem: Int?) {
        recyclerView = view.findViewById(R.id.recyclerView)
        // setHasFixedSize(true), то подразумеваете, что размеры самого RecyclerView будет оставаться неизменными.
        // Если вы используете setHasFixedSize(false), то при каждом добавлении/удалении элементов RecyclerView
        // будет перепроверять свои размеры
        recyclerView.setHasFixedSize(false)

        // staggeredGridLayoutManager - шахматный порядок
        staggeredGridLayoutManager = StaggeredGridLayoutManager(column, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.setLayoutManager(staggeredGridLayoutManager)

        adapterList = AdapterList(this.requireActivity(), (activity as MainActivity?)!!.hashMap)
        recyclerView.adapter = adapterList









//        // этот интерфейс сработает тогда когда заполниться hashmap
//        // тут обновиться адаптер и измениться flagLoading
//        (activity as MainActivity).setInterFaceAdapter(object : MainActivity.InterFaceAdapter {
//            override fun myInterFaceAdapter() {
//                Log.e("333", "-зашел setInterFaceAdapter(object : MainActivity-")
//                adapterList.notifyDataSetChanged()
//                flagLoading = false
//            }
//        })

//        // слушатель RecyclerView для получения последнего видимомого элемента, чтобы использовать при повороте
//        val mScrollListener: RecyclerView.OnScrollListener = object : RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                // эта часть отвечает за срабатывание запроса на получение дополнительных данных для записи в hashMap
//                // totalCountItem переменная всегода равно размеру hashmap в который добавляется + 20
//                totalCountItem = staggeredGridLayoutManager?.itemCount!!
//
//                // эта часть отвечет только за передачу последнего видимомо элемента
//                lastVisibleItemPositions = staggeredGridLayoutManager?.findLastVisibleItemPositions(null)!!
//                //Log.e("zzz","-lastVisibleItemPositions=" + lastVisibleItemPositions.length);
//                (context as MainActivity).lastVisibleItem = getMaxPosition(lastVisibleItemPositions)
//
//                Log.e("333", "-проверка totalCountItem-" + totalCountItem +
//                        "-(context as MainActivity).lastVisibleItem-" + (context as MainActivity).lastVisibleItem)
//
//                // эта часть должна срабатывать при достижении прокрутки
//                // totalCountItem - общее, lastVisibleItem - последний видимый
//                if (flagLoading == false && (totalCountItem - 5) == (context as MainActivity).lastVisibleItem)
//                {
//                    // тут я запускаю новый запрос даных на сервер с offset
//                    val runnable = Runnable {
//                        Log.e("333", "-зашел offset-")
//                        z = z + 20 // переменная для увеличения значения offset
//
//                        val requestData: RequestData = RequestData()
//                        requestData.getData(z, context) /// + 20;
//                    }
//                    Handler(Looper.getMainLooper()).postDelayed(runnable, 100)
//
//                    flagLoading = true // и возвращаю flagLoading в исходное состояние
//                }
//            }
//            fun getMaxPosition(positions: IntArray): Int {
//                return positions[0]
//            }
//        }
//        recyclerView?.addOnScrollListener(mScrollListener)
//
//        val runnable = Runnable { // установка последнего элемента в главном потоке
//            try {
//                requireActivity().runOnUiThread {
//                    staggeredGridLayoutManager!!.scrollToPositionWithOffset(lastVisableItem!!, 0)
//                }
//            } catch (e: Exception) {
//                Log.e("333", "-try catch FragmentHome 1-$e")
//            }
//        }
//        Handler(Looper.getMainLooper()).postDelayed(runnable, 500)
    }



    fun getData() {


        val asyncHttpClient = AsyncHttpClient()

        val requestParams = RequestParams()
        requestParams.put("key", "e0ecba986417447ebbaa87aad9d31458")


        asyncHttpClient.get("https://api.rawg.io/api/games?$requestParams&dates=2019-09-01,2019-09-30&platforms=18,1,7", null, object : TextHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseString: String?) {
                Log.e("333", "-onSuccess-" + responseString)

                try {
                    val jsonObject: JSONObject = JSONObject(responseString)
                    val jsonArray = jsonObject.getJSONArray("results")


                    for (n in 0 until jsonArray.length()) {
                        val nameGame = jsonObject.getJSONArray("results").getJSONObject(n).getString("name")//-----

                        val arrayPlatforms: MutableList<String> = ArrayList() // массив для записи платформ у игры //------
                        // по platforms считаю от 0 до ... платформы игры
                        val platforms = jsonObject.getJSONArray("results").getJSONObject(n).getJSONArray("platforms")
                        for (z in 0 until platforms.length()) {
                            val platform = jsonObject.getJSONArray("results").getJSONObject(n).getJSONArray("platforms")
                                .getJSONObject(z).getJSONObject("platform").getString("name")
                            arrayPlatforms.add(platform)
                        }

                        val arrayStores: MutableList<String> = ArrayList() // массив для записи магазинов у игры //------
                        val stores = jsonObject.getJSONArray("results").getJSONObject(n).getJSONArray("stores")
                        for (i in 0 until stores.length()) {
                            val store = jsonObject.getJSONArray("results").getJSONObject(n).getJSONArray("stores")
                                .getJSONObject(i).getJSONObject("store").getString("name")
                            arrayStores.add(store)
                        }

                        val imgMain = jsonObject.getJSONArray("results").getJSONObject(n).getString("background_image") //------
                        val rating = jsonObject.getJSONArray("results").getJSONObject(n).getString("rating") //------

                        val ratingTop = jsonObject.getJSONArray("results").getJSONObject(n).getString("rating_top") //------
                        val added = jsonObject.getJSONArray("results").getJSONObject(n).getString("added") //------
                        val updated = jsonObject.getJSONArray("results").getJSONObject(n).getString("updated") //------

                        val arrayScreenShots: MutableList<String> = ArrayList() // массив для записи скринов у игры //------
                        // по platforms считаю от 0 до ... платформы игры
                        val screenshots = jsonObject.getJSONArray("results").getJSONObject(n).getJSONArray("short_screenshots")
                        for (y in 0 until screenshots.length()) {
                            val screenshot = jsonObject.getJSONArray("results").getJSONObject(n).getJSONArray("short_screenshots")
                                .getJSONObject(y).getString("image")
                            arrayScreenShots.add(screenshot)
                        }

                        val arrayGenres: MutableList<String> = ArrayList() // массив для записи жанров у игры //------
                        // по platforms считаю от 0 до ... платформы игры
                        val genres = jsonObject.getJSONArray("results").getJSONObject(n).getJSONArray("genres")
                        for (g in 0 until genres.length()) {
                            val genre = jsonObject.getJSONArray("results").getJSONObject(n).getJSONArray("genres")
                                .getJSONObject(g).getString("name")
                            arrayGenres.add(genre)
                        }

                        (activity as MainActivity).hashMap.set(n, ObjectList(nameGame, arrayPlatforms, arrayStores, imgMain,
                        rating, ratingTop, added, updated, arrayScreenShots, arrayGenres))
                    }

                    (context as MainActivity?)?.runOnUiThread {
                       adapterList?.notifyDataSetChanged()
                    }

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
}