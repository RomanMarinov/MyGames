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
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import android.view.Gravity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.dev_marinov.mygames.R
import com.dev_marinov.mygames.data.Games
import com.dev_marinov.mygames.data.ObjectListDetail
import com.dev_marinov.mygames.data.Platforms
import com.dev_marinov.mygames.databinding.FragmentListBinding
import com.dev_marinov.mygames.databinding.WindowsAlertdialogBinding

import kotlin.collections.HashMap
import android.view.WindowManager

class FragmentList : Fragment() {

    private lateinit var bindingFragmentList: FragmentListBinding

    lateinit var model: SharedViewModel
    lateinit var viewModelListGames: ViewModelListGames
    lateinit var viewModelTotalCountItem: ViewModelTotalCountItem
    lateinit var viewModelLastVisibleItemPosition2: ViewModelLastVisibleItemPosition2
    lateinit var viewModelFlagLoading: ViewModelFlagLoading
    lateinit var viewModelPage: ViewModelPage
    lateinit var viewModelFromAndToString: ViewModelFromAndToString
    lateinit var viewModelStatusDialogDate: ViewModelStatusDialogDate

    val apiKey = "e0ecba986417447ebbaa87aad9d31458"

    var adapterList: AdapterList? = null // адаптер для главного списка
    var myViewGroup: ViewGroup? = null // контейнер для перезаписи макета при смене конфигурации
    var myLayoutInflater: LayoutInflater? = null // инфлятор

    var onDateSetListenerFrom: DatePickerDialog.OnDateSetListener? = null // слушатель даты слева
    var onDateSetListenerTo: DatePickerDialog.OnDateSetListener? = null // слушатель даты справа

    var staggeredGridLayoutManager: StaggeredGridLayoutManager? = null

    var lastVisibleItemArrayPositions2: IntArray? = null // для первого слушателя recyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        myViewGroup = container
        myLayoutInflater = inflater

        return initInterFace()
    }

    private fun initInterFace () : View {
        Log.e("333","=FragmentList=")

        if (myViewGroup != null) { // при пересоздании макета, если он не пуст, он будет очищен
            myViewGroup?.removeAllViewsInLayout()
        }
        // получить int ориентации
        val orientation = requireActivity().resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            bindingFragmentList = DataBindingUtil.inflate(myLayoutInflater!!, R.layout.fragment_list, myViewGroup, false)
        } else {
            bindingFragmentList = DataBindingUtil.inflate(myLayoutInflater!!, R.layout.fragment_list, myViewGroup, false)
        }
            myRecyclerLayoutManagerAdapter()

        return bindingFragmentList.root
    }

    // метод для установки recyclerview, GridLayoutManager и AdapterListHome
    private fun myRecyclerLayoutManagerAdapter() {

        viewModelListGames = ViewModelProvider(this)[ViewModelListGames::class.java]
        viewModelTotalCountItem = ViewModelProvider(this)[ViewModelTotalCountItem::class.java]
        viewModelLastVisibleItemPosition2 = ViewModelProvider(this)[ViewModelLastVisibleItemPosition2::class.java]
        viewModelFlagLoading = ViewModelProvider(this)[ViewModelFlagLoading::class.java]
        viewModelPage = ViewModelProvider(this)[ViewModelPage::class.java]
        viewModelFromAndToString = ViewModelProvider(this)[ViewModelFromAndToString::class.java]
        viewModelStatusDialogDate = ViewModelProvider(this)[ViewModelStatusDialogDate::class.java]

        model = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        bindingFragmentList.tvMyGames.text = resources.getString(R.string.my_games)
        bindingFragmentList.btSetRangeDate.text = resources.getString(R.string.set_button)

        // при создании макета проверяем статус был ли перед созданием макета открыт диалог
            // если да (true), значит запустим его снова
        if(viewModelStatusDialogDate.status) {
           myAlertDialogMain()
        }

        adapterList = AdapterList()

        staggeredGridLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        bindingFragmentList.recyclerView.apply {
            setHasFixedSize(false)
            layoutManager = staggeredGridLayoutManager
            adapter = adapterList
        }

        // при первой загрузке приложения, если массив с играми пустой, то запустить viewModel
        // который вызовет сетевой метод запроса данных об играх
        if (viewModelListGames.getHashMapGames().value == null) {
            // даты по умолчанию при первой загрузке
            viewModelFromAndToString.dataFromString = String.format("2019-09-01,")
            viewModelFromAndToString.dataToString = String.format("2019-09-30")

            (context as MainActivity?)?.runOnUiThread { // в главном потоке прогрессбар
                bindingFragmentList.progressBar.visibility = View.VISIBLE
            }
        } else {
            Log.e("333", "FragmentHome arrayList.size()  НЕ ПУСТОЙ=")
        }

        viewModelListGames.getHashMapGames().observe(requireActivity(), object :Observer<HashMap<Int,Games>>{
            override fun onChanged(hashMap: HashMap<Int, Games>?) {

                bindingFragmentList.progressBar.visibility = View.GONE
                viewModelFlagLoading.flagLoading = false

                if(hashMap != null) {

                    adapterList!!.setUpdateData(hashMap)
                    adapterList!!.notifyDataSetChanged()
                } else {
                    Toast.makeText(requireContext(), "error hashMap == null", Toast.LENGTH_SHORT).show()
                }
            }

        })

        // сработает при первой загрузке когда массив с данными пустой
        // проверка hashMapTemp временного массива на пустоту, чтобы при повороте экрана не вызвать
        // сетевой запрос во viewModelListGames, чтобы не дублировать запись в массив
        if (viewModelListGames.hashMapTemp.size == 0) {
            viewModelListGames.makeApiCall(apiKey, viewModelFromAndToString.dataFromString as String,
                viewModelFromAndToString.dataToString as String, viewModelPage.page, viewModelFlagLoading)

        }

        adapterList!!.setOnItemClickListener(object : AdapterList.onItemClickListener {
            override fun onItemClick(position: Int) {

                getClickPosition(position)
                Log.e("333","-position=" +position )

            }
        })

        bindingFragmentList.btSetRangeDate.setOnClickListener { // кнопка выбора/установка диапазона дат для получения новых данных
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

                        viewModelListGames.makeApiCall(apiKey, viewModelFromAndToString.dataFromString as String,
                            viewModelFromAndToString.dataToString as String, viewModelPage.page, viewModelFlagLoading)

                    }
                    Handler(Looper.getMainLooper()).postDelayed(runnable, 100)

                    (context as MainActivity?)?.runOnUiThread {
                        bindingFragmentList.progressBar.visibility = View.VISIBLE
                    }

                   viewModelFlagLoading.flagLoading = true // и возвращаю flagLoading в исходное состояние
                }
            }

            fun getMaxPosition(positions: IntArray): Int {
                return positions[positions.size-1]
            }
        }
        bindingFragmentList.recyclerView.addOnScrollListener(mScrollListener)
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

            // запись врменного массива myHashMapDetail в hashMapGamesDetail
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

    private fun myAlertDialogMain() { // установка дат
        val bindingAlertDialogDate: WindowsAlertdialogBinding = DataBindingUtil
            .inflate(LayoutInflater.from(requireActivity()),
                R.layout.windows_alertdialog, myViewGroup, false)

        bindingAlertDialogDate.btCancel.text = resources.getString(R.string.cancel)
        bindingAlertDialogDate.btOk.text = resources.getString(R.string.ok)

        val dialog = Dialog(requireActivity())
        dialog.setContentView(bindingAlertDialogDate.root)
        dialog.setCancelable(true) // чтобы можно было просто кликнув полю сбросить окно

        // установка ширины диалога, а то больше ничем не изменить ширину диалога
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
//        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        dialog.show()
        dialog.window!!.attributes = lp

        // костыль для повторного отображения диалога при повороте экрана
            viewModelStatusDialogDate.status = true
        dialog.setOnDismissListener {
            Log.e("333", "-dialog.setOnDismissListener=")
            viewModelStatusDialogDate.status = false
        }

        // отображаем слева в textview например "2019-09-01," только без запятой, т.е. "2019-09-01"
        bindingAlertDialogDate.tvDateFrom.text = String.format(viewModelFromAndToString.dataFromString)
            .replace(",","")

        // клик на textview слева
        bindingAlertDialogDate.tvDateFrom.setOnClickListener {
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
            // запись слева когда мы покрутили календарь
        onDateSetListenerFrom = DatePickerDialog.OnDateSetListener { datePicker, i, i2, i3 ->
            Log.e("333", "-datePicker="+ "-i-" + i + "-i2-" + i2 + "-i3-" + i3)

            // получаем из метода getDate строку с переработанной датой под сетевой запрос апи
            viewModelFromAndToString.dataFromString = getDate(i, i2, i3) + ","
            // получаем из метода getDate строку с переработанной датой для отображения в tvDateFrom
            bindingAlertDialogDate.tvDateFrom.text = getDate(i, i2, i3)
        }

            // установка даты для отображения в tvDateTo
        bindingAlertDialogDate.tvDateTo.text = String.format(viewModelFromAndToString.dataToString)

        bindingAlertDialogDate.tvDateTo.setOnClickListener {
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

            // получаем из метода getDate строку с переработанной датой под сетевой запрос апи
            viewModelFromAndToString.dataToString = getDate(i, i2, i3)
            // получаем из метода getDate строку с переработанной датой для отображения в tvDateTo
            bindingAlertDialogDate.tvDateTo.text = getDate(i, i2, i3)
        }

        bindingAlertDialogDate.btCancel.setOnClickListener {
            dialog.dismiss()
            dialog.cancel()
        }
        bindingAlertDialogDate.btOk.setOnClickListener{
            dialog.dismiss()

            // удаляем из даты слева тире и запятую
            val stringFrom1 = viewModelFromAndToString.dataFromString.replace("-","")
            val stringFrom2 = stringFrom1.replace(",","").toIntOrNull()
            // удаляем из даты справа тире
            val stringTo = viewModelFromAndToString.dataToString.replace("-","").toIntOrNull()

            Log.e("333","=ДО stringFrom2="+ stringFrom2 + "=stringTo=" + stringTo)
            // проверка на то, что пользовтель ввел первую дату меньше чем вторую
            if ((stringFrom2!! - stringTo!!) <= 0) {

                    viewModelListGames.getHashMapGames().value!!.clear()
                    viewModelListGames.hashMapTemp.clear()

                Log.e("333","=ПОСЛЕ stringFrom2="+ stringFrom2 + "=stringTo=" + stringTo)
                Log.e("333","=dataFromString="+ viewModelFromAndToString.dataFromString + "=dataToString=" + viewModelFromAndToString.dataToString)

                // https://api.rawg.io/api/games?key=YOUR_API_KEY&dates=2019-09-01,2019-09-30&platforms=18,1,7
                // вызов нового апи запроса в сеть для получения данных об играх
                viewModelListGames.makeApiCall(apiKey, viewModelFromAndToString.dataFromString as String,
                    viewModelFromAndToString.dataToString as String, 1, viewModelFlagLoading)

                (context as MainActivity?)?.runOnUiThread {
                    bindingFragmentList.progressBar.visibility = View.VISIBLE
                }

            }else{
                val toast = Toast.makeText(activity, "The first date must be less than the second date", Toast.LENGTH_LONG)
                toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 0)
                toast.show()
            }
        }
    }

    // функция переработки полученной даты календаря (где нет нулей, там поставил)
    private fun getDate(i: Int, i2: Int, i3: Int): String {
        // i - год, i2 - месяц(январь с нуля идет отсчет), i3 - день
        val i2 = i2 + 1 // + 1

        // если месяц не входит в диапазон от 10 до 12, а день входит от 10 до 31
        if (i2 !in 10..12 && i3 in 10..31) return String.format("$i" + "-" + "0$i2" + "-" + "$i3")
        // если месяц входит в диапазон от 10 до 12, а день не входит от 10 до 31
        if (i2 in 10..12 && i3 !in 10..31) return String.format("$i" + "-" + "$i2" + "-" + "0$i3")
        // если месяц входит в диапазон от 10 до 12, и день входит от 10 до 31
        if (i2 in 10..12 && i3 in 10..31) return String.format("$i" + "-" + "$i2" + "-" + "$i3")
        // если месяц не входит в диапазон от 10 до 12, и день не входит от 10 до 31
        if (i2 !in 10..12 && i3 !in 10..31) return String.format("$i" + "-" + "0$i2" + "-" + "0$i3")

        return ""
    }

}