package com.dev_marinov.mygames.presentation

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
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import android.view.Gravity
import android.widget.ProgressBar
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.dev_marinov.mygames.R
import com.dev_marinov.mygames.data.Games
import com.dev_marinov.mygames.data.ObjectListDetail
import com.dev_marinov.mygames.data.Platforms

import kotlin.collections.HashMap


class FragmentList : Fragment() {

    lateinit var model: SharedViewModel
    lateinit var viewModelListGames: ViewModelListGames
    lateinit var viewModelTotalCountItem: ViewModelTotalCountItem
    lateinit var viewModelLastVisibleItemPosition2: ViewModelLastVisibleItemPosition2
    lateinit var viewModelFlagLoading: ViewModelFlagLoading
    lateinit var viewModelPage: ViewModelPage
    lateinit var viewModelFromAndToString: ViewModelFromAndToString


    val apiKey = "e0ecba986417447ebbaa87aad9d31458"
    lateinit var recyclerView: RecyclerView
    var adapterList: AdapterList? = null // адаптер для главного списка
    var myViewGroup: ViewGroup? = null // контейнер для перезаписи макета при смене конфигурации
    var myLayoutInflater: LayoutInflater? = null // инфлятор
    lateinit var btSetRangeDate: Button // кнопка выбора/установка диапазона дат для получения новых данных
    lateinit var btCancel: Button // кнопка отмены диалога
    lateinit var btOk: Button // кнопка подтверждения дат
    lateinit var tvDateFrom: TextView // отображаются даты
    lateinit var tvDateTo: TextView // отображаются даты
    lateinit var progressBar: ProgressBar
    //var dataFromString: String? = null // переменные просто для работы с датами
    //var dataToString: String? = null // переменные просто для работы с датами

    var onDateSetListenerFrom: DatePickerDialog.OnDateSetListener? = null // слушатель даты слева
    var onDateSetListenerTo: DatePickerDialog.OnDateSetListener? = null // слушатель даты справа

    var staggeredGridLayoutManager: StaggeredGridLayoutManager? = null

    var lastVisibleItemArrayPositions2: IntArray? = null // для первого слушателя recyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        myViewGroup = container
        myLayoutInflater = inflater

        return initInterface()
    }

    fun initInterface () : View {
        Log.e("333","=FragmentList=")

        val view: View
        if (myViewGroup != null) { // при пересоздании макета, если он не пуст, он будет очищен
            myViewGroup?.removeAllViewsInLayout()
        }
        // получить int ориентации
        val orientation = requireActivity().resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            view = layoutInflater.inflate(R.layout.fragment_list, myViewGroup, false)
            myRecyclerLayoutManagerAdapter(view, 2)
        } else {
            view = layoutInflater.inflate(R.layout.fragment_list, myViewGroup, false)
            myRecyclerLayoutManagerAdapter(view, 2)
        }



        return view
    }

    // метод для установки recyclerview, GridLayoutManager и AdapterListHome
    fun myRecyclerLayoutManagerAdapter(view: View, column: Int) {

        viewModelListGames = ViewModelProvider(this).get(ViewModelListGames::class.java)
        viewModelTotalCountItem = ViewModelProvider(this).get(ViewModelTotalCountItem::class.java)
        viewModelLastVisibleItemPosition2 = ViewModelProvider(this).get(ViewModelLastVisibleItemPosition2::class.java)
        viewModelFlagLoading = ViewModelProvider(this).get(ViewModelFlagLoading::class.java)
        viewModelPage = ViewModelProvider(this).get(ViewModelPage::class.java)
        viewModelFromAndToString = ViewModelProvider(this).get(ViewModelFromAndToString::class.java)

        model = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        recyclerView = view.findViewById(R.id.recyclerView)
        btSetRangeDate = view.findViewById(R.id.btSetRangeDate)
        progressBar = view.findViewById(R.id.progressBar)

        recyclerView.setHasFixedSize(false)

        staggeredGridLayoutManager = StaggeredGridLayoutManager(column, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.setLayoutManager(staggeredGridLayoutManager)

        adapterList = AdapterList()
        recyclerView.adapter = adapterList


        // при первой загрузке приложения, если массив с играми пустой, то запустить viewmodel
        // который вызовет сетевой метод запроса данных об играх
        if (viewModelListGames.getHashMapGames().value == null) {
            // даты по умолчанию при первой загрузке
            viewModelFromAndToString.dataFromString = String.format("2019-09-01,")
            viewModelFromAndToString.dataToString = String.format("2019-09-30")

//            // говорим viewmodel чтобы запросить данные по сети
//            viewModelListGames.setParams(viewModelFromAndToString.dataFromString as String,
//                viewModelFromAndToString.dataToString as String, 1, viewModelFlagLoading)

            (context as MainActivity?)?.runOnUiThread { // в главном потоке прогрессбар
                progressBar.visibility = View.VISIBLE
            }
        } else {
            Log.e("333", "FragmentHome arrayList.size()  НЕ ПУСТОЙ=")
        }


        viewModelListGames.getHashMapGames().observe(requireActivity(), object :Observer<HashMap<Int,Games>>{
            override fun onChanged(hashMap: HashMap<Int, Games>?) {

                progressBar.visibility = View.GONE
                viewModelFlagLoading.flagLoading = false

                    Log.e("333","=t="+hashMap)
                if(hashMap != null) {

                    adapterList!!.setUpdateData(hashMap)
                    adapterList!!.notifyDataSetChanged()
                } else {
                    Toast.makeText(requireContext(), "error hashMap == null", Toast.LENGTH_SHORT).show()

                }
            }

        })
        viewModelListGames.makeApicall(apiKey, viewModelFromAndToString.dataFromString as String,
            viewModelFromAndToString.dataToString as String, 1, viewModelFlagLoading)






        adapterList!!.setOnItemClickListener(object : AdapterList.onItemClickListener {
            override fun onItemClick(position: Int) {

                getClickPosition(position)
            Log.e("333","-position=" +position )

            }
        })

//        // наблюдатель об изменениях в массиве, чтобы передать его в адаптер и обновить его
//        viewModelListGames.getHashMapGames().observe(requireActivity(), androidx.lifecycle.Observer {
//            it.let { adapterList!!.refreshListGames(it) } // it - обновленный список
//        })

//        viewModelListGames.getHashMapGames().observe(requireActivity(), object :Observer<ObjectListGames> {
//            override fun onChanged(t: ObjectListGames?) {
//                adapterList!!.setUpdateData(t!!.items)
//            }
//        })

//        // метод интерфейса сработает когда мы получим данные от сети обновить его еще раз
//        (context as MainActivity).setMyInterFaceGames(object : MainActivity.MyInterFaceGames {
//            override fun methodMyInterFaceGames() {
//                adapterList!!.notifyDataSetChanged()
//
//                progressBar.visibility = View.GONE
//            }
//        })

        btSetRangeDate.setOnClickListener { // кнопка выбора/установка диапазона дат для получения новых данных
            myAlertDialogMain()
        }

        // второй слушатель RecyclerView реализации offset чтобы подгружать данные при скроле
        val mScrollListener: RecyclerView.OnScrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                // эта часть отвечает за срабатывание запроса на получение дополнительных данных для записи в hashMap
                // totalCountItem переменная всегда равно размеру hashmap в который добавляется + 20
                viewModelTotalCountItem.totalCountItem = staggeredGridLayoutManager?.itemCount!!

                // эта часть отвечет только за передачу последнего видимомо элемента
                lastVisibleItemArrayPositions2 = staggeredGridLayoutManager?.findLastVisibleItemPositions(null)
                viewModelLastVisibleItemPosition2.lastVisibleItemPosition2 = getMaxPosition(lastVisibleItemArrayPositions2!!)

                if (viewModelFlagLoading.flagLoading == false
                    && (viewModelTotalCountItem.totalCountItem - 5) ==  viewModelLastVisibleItemPosition2.lastVisibleItemPosition2)
                {
                    // тут я запускаю новый запрос даных на сервер с offset
                    val runnable = Runnable {
                        Log.e("333", "-зашел dataFromString-" + viewModelFromAndToString.dataFromString
                        + "=dataToString=" + viewModelFromAndToString.dataToString
                        + "=viewModelPage.page=" + viewModelPage.page
                        + "=viewModelFlagLoading=" + viewModelFlagLoading)
                        viewModelPage.page = viewModelPage.page + 1// переменная для увеличения значения offset
                        Log.e("333", "-зашел offset-")

                        viewModelListGames.makeApicall(apiKey, viewModelFromAndToString.dataFromString as String,
                            viewModelFromAndToString.dataToString as String, viewModelPage.page, viewModelFlagLoading)

                    }
                    Handler(Looper.getMainLooper()).postDelayed(runnable, 100)

                    (context as MainActivity?)?.runOnUiThread {
                        progressBar.visibility = View.VISIBLE
                    }

                   viewModelFlagLoading.flagLoading = true // и возвращаю flagLoading в исходное состояние
                }
            }

            fun getMaxPosition(positions: IntArray): Int {
                return positions[positions.size-1]
            }
        }
        recyclerView.addOnScrollListener(mScrollListener)
    }


    fun getClickPosition(position: Int) {

        val myHashMapDetail: HashMap<Int, ObjectListDetail> = HashMap()

        myHashMapDetail[0] = ObjectListDetail(
            viewModelListGames.getHashMapGames().value!![position]!!.name!!,
            viewModelListGames.getHashMapGames().value!![position]!!.released!!,
            viewModelListGames.getHashMapGames().value!![position]!!.rating!!,
            viewModelListGames.getHashMapGames().value!![position]!!.rating_top!!,
            viewModelListGames.getHashMapGames().value!![position]!!.added!!,
            viewModelListGames.getHashMapGames().value!![position]!!.updated!!,
            viewModelListGames.getHashMapGames().value!![position]!!.short_screenshots,
            viewModelListGames.getHashMapGames().value!![position]!!.platforms as MutableList<Platforms>

        )

        val hashMapGamesDetail: MutableLiveData<HashMap<Int, ObjectListDetail>> = MutableLiveData()

        hashMapGamesDetail.value = myHashMapDetail

        val fragmentDetail = FragmentDetail()

        model.sendMessage(myHashMapDetail)

        requireActivity().supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(
                R.anim.enter_right_to_left,
                R.anim.exit_right_to_left,
                R.anim.enter_left_to_right,
                R.anim.exit_right_to_left
            )
            .replace(R.id.llFragDetail, fragmentDetail, "llFragDetail")
            .addToBackStack("llFragDetail")
            .commit()

   }

    fun myAlertDialogMain() { // установка дат
        val dialog = Dialog(requireActivity())
        dialog.setContentView(R.layout.windows_alertdialog)

        dialog.setCancelable(true) // чтобы можно было просто кликнув полю сбросить окно
        dialog.show()

        tvDateFrom = dialog.findViewById(R.id.tvDateFrom)
        tvDateFrom.text = String.format(viewModelFromAndToString.dataFromString).replace(",","")

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
            if (i2 !in 10..12) {
                viewModelFromAndToString.dataFromString = String.format("$i" + "-" + "0$i2" + "-" + "$i3,")
            }
            if (i3 !in 10..31) {
                viewModelFromAndToString.dataFromString = String.format("$i" + "-" + "$i2" + "-" + "0$i3,")
            }
            if (i2 in 10..12 && i3 !in 10..31) {
                viewModelFromAndToString.dataFromString = String.format("$i" + "-" + "0$i2" + "-" + "0$i3,")
            }
        }

        tvDateTo = dialog.findViewById(R.id.tvDateTo)
        tvDateTo.text = String.format(viewModelFromAndToString.dataToString)

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
            viewModelFromAndToString.dataToString = String.format("$i" + "-" + "$i2" + "-" + "$i3")
            Log.e("333", "-проверка 1 dataToString=" + viewModelFromAndToString.dataToString)
            if (i2 !in 10..12) {
                viewModelFromAndToString.dataToString = String.format("$i" + "-" + "0$i2" + "-" + "$i3")
                Log.e("333", "-проверка 2 dataToString=" + viewModelFromAndToString.dataToString)
            }
            if (i3 !in 10..31) {
                viewModelFromAndToString.dataToString = String.format("$i" + "-" + "$i2" + "-" + "0$i3")
                Log.e("333", "-проверка 3 dataToString=" + viewModelFromAndToString.dataToString)
            }
            if (i2 in 10..12 && i3 !in 10..31) {
                viewModelFromAndToString.dataToString = String.format("$i" + "-" + "0$i2" + "-" + "0$i3")
                Log.e("333", "-проверка 4 dataToString=" + viewModelFromAndToString.dataToString)
            }
            Log.e("333", "-проверка 5 dataToString=" + viewModelFromAndToString.dataToString)
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
            val stringFrom1 = viewModelFromAndToString.dataFromString.replace("-","")
            val stringFrom2 = stringFrom1.replace(",","").toIntOrNull()

            val stringTo = viewModelFromAndToString.dataToString.replace("-","").toIntOrNull()

            Log.e("333","=ДО stringFrom2="+ stringFrom2 + "=stringTo=" + stringTo)
            if ((stringFrom2!! - stringTo!!) <= 0) {
                //(activity as MainActivity).hashMap.clear()
                    viewModelListGames.getHashMapGames().value!!.clear()
                Log.e("333","=ПОСЛЕ stringFrom2="+ stringFrom2 + "=stringTo=" + stringTo)
                Log.e("333","=dataFromString="+ viewModelFromAndToString.dataFromString + "=dataToString=" + viewModelFromAndToString.dataToString)

                viewModelListGames.makeApicall(apiKey, viewModelFromAndToString.dataFromString as String,
                    viewModelFromAndToString.dataToString as String, 1, viewModelFlagLoading)

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