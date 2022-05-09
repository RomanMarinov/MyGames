package com.dev_marinov.mygames

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.res.Configuration
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.RequestParams
import com.loopj.android.http.TextHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import android.view.Gravity
import android.widget.ProgressBar
import androidx.recyclerview.widget.StaggeredGridLayoutManager


class FragmentList : Fragment() {

    // api =e0ecba986417447ebbaa87aad9d31458
    lateinit var recyclerView: RecyclerView
    var adapterList: AdapterList? = null
    var myViewGroup: ViewGroup? = null
    var myLayoutInflater: LayoutInflater? = null
    var gridLayoutManager: GridLayoutManager? = null
    lateinit var btSetRangeDate: Button
    lateinit var btCancel: Button
    lateinit var btOk: Button
    lateinit var tvDateFrom: TextView
    lateinit var tvDateTo: TextView
    lateinit var progressBar: ProgressBar
    var dataFromString: String? = null
    var dataToString: String? = null

    var onDateSetListenerFrom: DatePickerDialog.OnDateSetListener? = null
    var onDateSetListenerTo: DatePickerDialog.OnDateSetListener? = null

    var staggeredGridLayoutManager: StaggeredGridLayoutManager? = null
    //var lastVisibleItemPositions: IntArray = IntArray()
//    val arrayListScreenShos: MutableList<String> = ArrayList()



    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
    var lastVisibleItemPositions: IntArray? = null
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
    var lastVisibleItemPositions2: IntArray? = null



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

            myRecyclerLayoutManagerAdapter(view, 2, (activity as MainActivity?)?.lastVisibleItemPositions)
        } else {
            view = layoutInflater.inflate(R.layout.fragment_list, myViewGroup, false)

            myRecyclerLayoutManagerAdapter(view, 2, (activity as MainActivity?)?.lastVisibleItemPositions)
        }

        if ((activity as MainActivity?)?.hashMap?.size == 0) {


//            tvDateFrom.text = String.format("2019-09-01,")
//            tvDateTo.text = String.format("2019-09-30")

            dataFromString = String.format("2019-09-01,")
            dataToString = String.format("2019-09-30")

            getData(dataFromString as String, dataToString as String, 1)
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
        btSetRangeDate = view.findViewById(R.id.btSetRangeDate)
        progressBar = view.findViewById(R.id.progressBar)

        recyclerView.setHasFixedSize(false)

        staggeredGridLayoutManager = StaggeredGridLayoutManager(column, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.setLayoutManager(staggeredGridLayoutManager)

//        gridLayoutManager = GridLayoutManager(requireActivity(), column)
//        recyclerView.layoutManager = gridLayoutManager

        adapterList = AdapterList(this.requireActivity(), (activity as MainActivity?)!!.hashMap)
        recyclerView.adapter = adapterList

        btSetRangeDate.setOnClickListener {
            myAlertDialogMain()
        }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

                lastVisibleItemPositions = staggeredGridLayoutManager!!.findFirstVisibleItemPositions(null)

                (context as MainActivity).lastVisibleItemPositions = getMaxPosition(lastVisibleItemPositions!!)

////////////////////////////////////////////////////
//                (context as MainActivity).totalCountItem = staggeredGridLayoutManager?.itemCount!!
//                (context as MainActivity).lastVisibleItemPositions = staggeredGridLayoutManager?.findLastVisibleItemPositions()!!
//
////////////////////////////////////////////////////
            }

             fun getMaxPosition(positions: IntArray): Int {
                return positions[0]
            }
        })
///////////////////////////////////////////////////////////////////////////////////////////////////////////////

        // слушатель RecyclerView для получения последнего видимомого элемента, чтобы использовать при повороте
        val mScrollListener: RecyclerView.OnScrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                // эта часть отвечает за срабатывание запроса на получение дополнительных данных для записи в hashMap
                // totalCountItem переменная всегда равно размеру hashmap в который добавляется + 20
                (context as MainActivity).totalCountItem = staggeredGridLayoutManager?.itemCount!!

                // эта часть отвечет только за передачу последнего видимомо элемента
                lastVisibleItemPositions2 = staggeredGridLayoutManager?.findLastVisibleItemPositions(null)
                //Log.e("zzz","-lastVisibleItemPositions=" + lastVisibleItemPositions.length);

                (context as MainActivity).lastVisibleItemPosit = getMaxPosition(lastVisibleItemPositions2!!)


                Log.e("333", "-проверка totalCountItem-" + (context as MainActivity).totalCountItem +
                        " (context as MainActivity).lastVisibleItemPosit-" +  (context as MainActivity).lastVisibleItemPosit)

                // эта часть должна срабатывать при достижении прокрутки
                // totalCountItem - общее, lastVisibleItem - последний видимый




                if ((context as MainActivity).flagLoading == false
                    && ((context as MainActivity).totalCountItem - 5) ==  (context as MainActivity).lastVisibleItemPosit)
                {
                    // тут я запускаю новый запрос даных на сервер с offset
                    val runnable = Runnable {
                        Log.e("333", "-зашел offset-")
                        (context as MainActivity).page = (context as MainActivity).page + 1// переменная для увеличения значения offset

                        getData(dataFromString as String, dataToString as String, (context as MainActivity).page) /// + 20;
                    }
                    Handler(Looper.getMainLooper()).postDelayed(runnable, 100)

                    (context as MainActivity).flagLoading = true // и возвращаю flagLoading в исходное состояние
                }
            }


            fun getMaxPosition(positions: IntArray): Int {
                return positions[positions.size-1]
            }
        }
        recyclerView.addOnScrollListener(mScrollListener)

        /////////////////////////////////////////////////////////////////


//        // слушатель RecyclerView для получения последнего видимомого элемента, чтобы использовать при повороте
//        val mScrollListener: RecyclerView.OnScrollListener = object : RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                // эта часть отвечает за срабатывание запроса на получение дополнительных данных для записи в hashMap
//                // totalCountItem переменная всегда равно размеру hashmap в который добавляется + 20
//                (context as MainActivity).totalCountItem = gridLayoutManager?.itemCount!!
//
//                // эта часть отвечет только за передачу последнего видимомо элемента
//                (context as MainActivity).lastVisibleItemPositions = gridLayoutManager?.findLastVisibleItemPosition()!!
//                //Log.e("zzz","-lastVisibleItemPositions=" + lastVisibleItemPositions.length);
//
//                Log.e("333", "-проверка totalCountItem-" + (context as MainActivity).totalCountItem +
//                        "-(context as MainActivity).lastVisibleItemPositions-" + (context as MainActivity).lastVisibleItemPositions)
//
//                // эта часть должна срабатывать при достижении прокрутки
//                // totalCountItem - общее, lastVisibleItem - последний видимый
//                if ((context as MainActivity).flagLoading == false
//                    && ((context as MainActivity).totalCountItem - 5) == (context as MainActivity).lastVisibleItemPositions)
//                {
//                    // тут я запускаю новый запрос даных на сервер с offset
//                    val runnable = Runnable {
//                        Log.e("333", "-зашел offset-")
//                        (context as MainActivity).page = (context as MainActivity).page + 1// переменная для увеличения значения offset
//
//                        getData(dataFromString as String, dataToString as String, (context as MainActivity).page!!) /// + 20;
//                    }
//                    Handler(Looper.getMainLooper()).postDelayed(runnable, 100)
//
//                    (context as MainActivity).flagLoading = true // и возвращаю flagLoading в исходное состояние
//                }
//            }
//        }
//        recyclerView.addOnScrollListener(mScrollListener)
//
        val runnable = Runnable { // установка последнего элемента в главном потоке
            try {
                requireActivity().runOnUiThread {
                    staggeredGridLayoutManager!!.scrollToPositionWithOffset(lastVisableItem!!, 0)
                }
            } catch (e: Exception) {
                Log.e("333", "-try catch FragmentHome 1-$e")
            }
        }
        Handler(Looper.getMainLooper()).postDelayed(runnable, 500)
    }



    fun getData(dataFrom: String, dataTo: String, page: Int) {

        progressBar.visibility = View.VISIBLE

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

                        (activity as MainActivity).hashMap.set((activity as MainActivity).hashMap.size, ObjectList(nameGame, arrayPlatforms,  released, imgMain,
                        rating, ratingTop, added, updated, arrayListScreenShots))

                    }


                    (context as MainActivity?)?.runOnUiThread {
                        adapterList?.notifyDataSetChanged()

                        progressBar.visibility = View.GONE
                    }

                    (context as MainActivity).flagLoading = false

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

    fun myAlertDialogMain() {
        val dialog = Dialog(requireActivity())
        dialog.setContentView(R.layout.windows_alertdialog)

        dialog.setCancelable(true) // чтобы можно было просто кликнув полю сбросить окно
        dialog.show()

        tvDateFrom = dialog.findViewById(R.id.tvDateFrom)

        tvDateFrom.text = String.format(dataFromString!!).replace(",","")

        tvDateFrom.setOnClickListener {
        val calendar: Calendar = Calendar.getInstance()
            val yearFrom = calendar.get(Calendar.YEAR)
            val monthFrom = calendar.get(Calendar.MONTH)
            val dayFrom = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                requireActivity(),
                android.R.style.Theme_Holo_Dialog,
                onDateSetListenerFrom, yearFrom, monthFrom, dayFrom)
            datePickerDialog.window?.setBackgroundDrawable(ColorDrawable(R.drawable.gradient))
            datePickerDialog.show()
        }

        onDateSetListenerFrom = DatePickerDialog.OnDateSetListener { datePicker, i, i2, i3 ->
            Log.e("333", "-datePicker=" + datePicker + "-i-" + i + "-i2-" + i2 + "-i3-" + i3)

            // i - год, i2 - месяц(январь с нуля идет отсчет), i3 - день
            tvDateFrom.text = String.format("$i" + "-" + "$i2" + "-" + "$i3")
            dataFromString = String.format("$i" + "-" + "0$i2" + "-" + "0$i3,")
        }


        tvDateTo = dialog.findViewById(R.id.tvDateTo)

        tvDateTo.text = String.format(dataToString!!)

        tvDateTo.setOnClickListener {
            val calendar: Calendar = Calendar.getInstance()
            val yearFrom = calendar.get(Calendar.YEAR)
            val monthFrom = calendar.get(Calendar.MONTH)
            val dayFrom = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                requireActivity(),
                android.R.style.Theme_Holo_Dialog,
                onDateSetListenerTo, yearFrom, monthFrom, dayFrom)
            datePickerDialog.window?.setBackgroundDrawable(ColorDrawable(R.drawable.gradient))
            datePickerDialog.show()
        }
        onDateSetListenerTo = DatePickerDialog.OnDateSetListener { datePicker, i, i2, i3 ->
            Log.e("333", "-datePicker=" + datePicker + "-i-" + i + "-i2-" + i2 + "-i3-" + i3)

            // i - год, i2 - месяц(январь с нуля идет отсчет), i3 - день
            tvDateTo.text = String.format("$i" + "-" + "$i2" + "-" + "$i3")

            dataToString = String.format("$i" + "-" + "0$i2" + "-" + "0$i3")
        }


        btCancel = dialog.findViewById(R.id.btCancel)
        btOk = dialog.findViewById(R.id.btOk)

        btCancel.setOnClickListener {
            dialog.dismiss()
            dialog.cancel()
        }
        btOk.setOnClickListener{
            dialog.dismiss()

            // проверка на то, что пользовтель ввел первую дату меньше чем вторую
            val stringFrom1 = dataFromString!!.replace("-","")
            val stringFrom2 = stringFrom1.replace(",","").toIntOrNull()

            val stringTo = dataToString!!.replace("-","").toIntOrNull()

            if ((stringFrom2!! - stringTo!!) <= 0) {
                (activity as MainActivity).hashMap.clear()
                getData(dataFromString as String, dataToString as String, 1)

                (context as MainActivity?)?.runOnUiThread {
                    progressBar.visibility = View.VISIBLE
                }

            }else{

                val toast = Toast.makeText(activity, "The first date must be less than the second date", Toast.LENGTH_LONG)
                toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 0)
                toast.show()
            }



        }
    }



}