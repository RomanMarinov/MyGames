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
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import android.view.Gravity
import android.widget.ProgressBar
import androidx.recyclerview.widget.StaggeredGridLayoutManager


class FragmentList : Fragment() {

    // api =e0ecba986417447ebbaa87aad9d31458
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
    var dataFromString: String? = null // переменные просто для работы с датами
    var dataToString: String? = null // переменные просто для работы с датами

    var onDateSetListenerFrom: DatePickerDialog.OnDateSetListener? = null // слушатель даты слева
    var onDateSetListenerTo: DatePickerDialog.OnDateSetListener? = null // слушатель даты справа

    var staggeredGridLayoutManager: StaggeredGridLayoutManager? = null

    var lastVisibleItemArrayPositions1: IntArray? = null // для первого слушателя recyclerView
    var lastVisibleItemArrayPositions2: IntArray? = null // для первого слушателя recyclerView

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
            myRecyclerLayoutManagerAdapter(view, 2, (activity as MainActivity?)?.lastVisibleItemPosition1)
        } else {
            view = layoutInflater.inflate(R.layout.fragment_list, myViewGroup, false)
            myRecyclerLayoutManagerAdapter(view, 2, (activity as MainActivity?)?.lastVisibleItemPosition1)
        }

        if ((activity as MainActivity?)?.hashMap?.size == 0) {

            // даты по умолчанию при первой загрузке
            dataFromString = String.format("2019-09-01,")
            dataToString = String.format("2019-09-30")

            val requestData = RequestData() // запрос данных и передача параметров
            requestData.getData(requireActivity(), dataFromString as String, dataToString as String, 1)

            (context as MainActivity?)?.runOnUiThread { // в главном потоке прогрессбар
                progressBar.visibility = View.VISIBLE
            }

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

        adapterList = AdapterList(this.requireActivity(), (activity as MainActivity?)!!.hashMap)
        recyclerView.adapter = adapterList

        // метод интерфейса сработает когда мы получим данные от сети
        (context as MainActivity).setMyInterFaceGames(object : MainActivity.MyInterFaceGames{
            override fun methodMyInterFaceGames() {
                adapterList!!.notifyDataSetChanged()

                progressBar.visibility = View.GONE
            }
        })

        btSetRangeDate.setOnClickListener { // кнопка выбора/установка диапазона дат для получения новых данных
            myAlertDialogMain()
        }
        // первый слушатель для получения первого верхнего элемента для повторного использования при
            // смене ориентации
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

                lastVisibleItemArrayPositions1 = staggeredGridLayoutManager!!.findFirstVisibleItemPositions(null)
                (context as MainActivity).lastVisibleItemPosition1 = getMaxPosition(lastVisibleItemArrayPositions1!!)
            }

             fun getMaxPosition(positions: IntArray): Int {
                return positions[0]
            }
        })

        // второй слушатель RecyclerView реализации offset чтобы подгружать данные при скроле
        val mScrollListener: RecyclerView.OnScrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                // эта часть отвечает за срабатывание запроса на получение дополнительных данных для записи в hashMap
                // totalCountItem переменная всегда равно размеру hashmap в который добавляется + 20
                (context as MainActivity).totalCountItem = staggeredGridLayoutManager?.itemCount!!

                // эта часть отвечет только за передачу последнего видимомо элемента
                lastVisibleItemArrayPositions2 = staggeredGridLayoutManager?.findLastVisibleItemPositions(null)
                (context as MainActivity).lastVisibleItemPosition2 = getMaxPosition(lastVisibleItemArrayPositions2!!)

                if ((context as MainActivity).flagLoading == false
                    && ((context as MainActivity).totalCountItem - 5) ==  (context as MainActivity).lastVisibleItemPosition2)
                {
                    // тут я запускаю новый запрос даных на сервер с offset
                    val runnable = Runnable {
                        Log.e("333", "-зашел offset-")
                        (context as MainActivity).page = (context as MainActivity).page + 1// переменная для увеличения значения offset

                        val requestData = RequestData()
                        requestData.getData(requireActivity(), dataFromString as String, dataToString as String,
                            (context as MainActivity).page) /// + 20;
                    }
                    Handler(Looper.getMainLooper()).postDelayed(runnable, 100)

                    (context as MainActivity?)?.runOnUiThread {
                        progressBar.visibility = View.VISIBLE
                    }

                    (context as MainActivity).flagLoading = true // и возвращаю flagLoading в исходное состояние
                }
            }

            fun getMaxPosition(positions: IntArray): Int {
                return positions[positions.size-1]
            }
        }
        recyclerView.addOnScrollListener(mScrollListener)

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

                val requestData = RequestData()
                requestData.getData(
                    requireActivity(),
                    dataFromString as String,
                    dataToString as String,
                    1
                )

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