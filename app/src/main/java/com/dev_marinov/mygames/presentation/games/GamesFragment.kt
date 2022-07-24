package com.dev_marinov.mygames.presentation.games

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.res.Configuration
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.dev_marinov.mygames.R
import com.dev_marinov.mygames.data.ObjectListDetail
import com.dev_marinov.mygames.databinding.WindowsAlertdialogBinding
import com.dev_marinov.mygames.data.ObjectConvertDate
import com.dev_marinov.mygames.domain.DateConverter
import com.dev_marinov.mygames.databinding.FragmentGamesBinding
import com.dev_marinov.mygames.domain.Platforms
import com.dev_marinov.mygames.presentation.activity.MainActivity
import com.dev_marinov.mygames.presentation.detail.DetailFragment
import com.dev_marinov.mygames.presentation.detail.SharedViewModel

import kotlin.collections.HashMap

class GamesFragment : Fragment() {

    val apiKey = "e0ecba986417447ebbaa87aad9d31458"
    private lateinit var bindingFragmentGames: FragmentGamesBinding

    lateinit var sharedViewModel: SharedViewModel
    lateinit var gamesViewModel: GamesViewModel

    var onDateSetListenerFrom: DatePickerDialog.OnDateSetListener? = null // слушатель даты слева
    var onDateSetListenerTo: DatePickerDialog.OnDateSetListener? = null // слушатель даты справа


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return initInterFace(inflater, container)
    }

    private fun initInterFace(inflater: LayoutInflater, container: ViewGroup?): View {
        container?.let { container.removeAllViewsInLayout() }

        // получить int ориентации
        val orientation = requireActivity().resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            bindingFragmentGames = DataBindingUtil.inflate(inflater, R.layout.fragment_games, container, false)
        } else {
            bindingFragmentGames = DataBindingUtil.inflate(inflater, R.layout.fragment_games, container, false)
        }
            myRecyclerLayoutManagerAdapter(container)

        return bindingFragmentGames.root
    }

    // метод для установки recyclerview, GridLayoutManager и AdapterListHome
    private fun myRecyclerLayoutManagerAdapter(container: ViewGroup?) {

        gamesViewModel = ViewModelProvider(this)[GamesViewModel::class.java]
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        checkStatusAlertDialogDate(container)
        bindingFragmentGames.progressBar.visibility = View.VISIBLE

        val gamesAdapter = GamesAdapter()
        val staggeredGridLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        bindingFragmentGames.recyclerView.apply {
            setHasFixedSize(false)
            layoutManager = staggeredGridLayoutManager
            adapter = gamesAdapter
        }


        gamesViewModel.hashMapGames.observe(viewLifecycleOwner) {
            bindingFragmentGames.progressBar.visibility = View.GONE
            gamesViewModel.flagLoading = false
            it?.let {
                gamesAdapter.setUpdateData(it)
                gamesAdapter.notifyDataSetChanged()
            }
        }

        gamesAdapter.setOnItemClickListener(object : GamesAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {
                getClickPosition(position)
                Log.e("333","-position=" +position )
            }
        })

        bindingFragmentGames.btSetRangeDate.setOnClickListener { // кнопка выбора/установка диапазона дат для получения новых данных
            myAlertDialogMain(container)
        }

        // второй слушатель RecyclerView реализации offset чтобы подгружать данные при скроле
        val scrollListener: RecyclerView.OnScrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                // эта часть отвечает за срабатывание запроса на получение дополнительных данных для записи в hashMap
                // totalCountItem переменная всегда равно размеру hashmap в который добавляется + 20
                gamesViewModel.totalCountItem = staggeredGridLayoutManager.itemCount

                // эта часть отвечет только за передачу последнего видимомо элемента
               val lastVisibleItemArrayPositions2 = staggeredGridLayoutManager.findLastVisibleItemPositions(null)
                gamesViewModel.lastVisibleItemPosition = getMaxPosition(lastVisibleItemArrayPositions2!!)

                if (gamesViewModel.flagLoading == false
                    && (gamesViewModel.totalCountItem - 5) ==  gamesViewModel.lastVisibleItemPosition) {
                    // тут я запускаю новый запрос даных на сервер с offset
                    val runnable = Runnable {
                        gamesViewModel.page = gamesViewModel.page + 1// переменная для увеличения значения offset

                        gamesViewModel.getGames(apiKey, gamesViewModel.dataFromString,
                            gamesViewModel.dataToString, gamesViewModel.page)
                    }
                    Handler(Looper.getMainLooper()).postDelayed(runnable, 100)

                    (context as MainActivity?)?.runOnUiThread {
                        bindingFragmentGames.progressBar.visibility = View.VISIBLE
                    }

                    gamesViewModel.flagLoading = true // и возвращаю flagLoading в исходное состояние
                }
            }

            fun getMaxPosition(positions: IntArray): Int {
                return positions[positions.size-1]
            }
        }
        bindingFragmentGames.recyclerView.addOnScrollListener(scrollListener)
    }

    private fun checkStatusAlertDialogDate(container: ViewGroup?) {
        // при создании макета проверяем статус был ли перед созданием макета открыт диалог
        // если да (true), значит запустим его снова
        if(gamesViewModel.statusAlertDialogDate) {
            // обращаемся к вспомогательному классу диалога для запуска
            myAlertDialogMain(container)
            if(gamesViewModel.statusLeftDate) {
                getCalendar(onDateSetListenerFrom) // запускаем левый календарь
            }
            if(gamesViewModel.statusRightDate) {
                getCalendar(onDateSetListenerTo) // запускаем правый календарь
            }
        }
    }

    fun getClickPosition(position: Int) {

        val hashMapDetail: HashMap<Int, ObjectListDetail> = HashMap()
        gamesViewModel.hashMapGames.value!![position]?.let {
            hashMapDetail[0] = ObjectListDetail(
                it.name!!,
                it.released!!,
                it.rating!!,
                it.rating_top!!,
                it.added!!,
                it.updated!!,
                it.short_screenshots,
                it.platforms as MutableList<Platforms>
            )
        }

        val hashMapGamesDetail: MutableLiveData<HashMap<Int, ObjectListDetail>> = MutableLiveData()

            // запись врменного массива hashMapDetail в hashMapGamesDetail
        hashMapGamesDetail.value = hashMapDetail

        val fragmentDetail = DetailFragment()

        sharedViewModel.sendMessage(hashMapDetail)

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

    private fun myAlertDialogMain(container: ViewGroup?) { // установка дат
        val bindingAlertDialogDate: WindowsAlertdialogBinding = DataBindingUtil
            .inflate(LayoutInflater.from(requireActivity()),
                R.layout.windows_alertdialog, container, false)

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
        gamesViewModel.statusAlertDialogDate = true
        dialog.setOnDismissListener {
            Log.e("333", "-dialog.setOnDismissListener=")
            gamesViewModel.statusAlertDialogDate = false
        }

        // отображаем слева в textview например "2019-09-01," только без запятой, т.е. "2019-09-01"
        bindingAlertDialogDate.tvDateFrom.text = String.format(gamesViewModel.dataFromString)
            .replace(",","")

        // клик на textview слева
        bindingAlertDialogDate.tvDateFrom.setOnClickListener {
            gamesViewModel.statusLeftDate = true
            getCalendar(onDateSetListenerFrom)
            Log.e("333", "bindingAlertDialogDate.tvDateFro")
        }

            // запись слева когда мы покрутили календарь
        onDateSetListenerFrom = DatePickerDialog.OnDateSetListener { datePicker, year, month, day ->
            Log.e("333", "-datePicker="+ "-year-" + year + "-month-" + month + "-day-" + day)


                // передавать во вьюмодель

            // обращаемся к всопомгательному классу конвертации даты календаря
            val dateConverter = DateConverter()
            val objectConvertDate = ObjectConvertDate(year = year,month = month, day = day)
            // получаем из метода getDate строку с переработанной датой под сетевой запрос апи
            gamesViewModel.dataFromString = dateConverter.getName(objectConvertDate) + ","
            // получаем из метода getDate строку с переработанной датой для отображения в tvDateFrom
            bindingAlertDialogDate.tvDateFrom.text = dateConverter.getName(objectConvertDate)
        }

            // установка даты для отображения в tvDateTo
        bindingAlertDialogDate.tvDateTo.text = String.format(gamesViewModel.dataToString)

        bindingAlertDialogDate.tvDateTo.setOnClickListener {
            gamesViewModel.statusRightDate = true
            getCalendar(onDateSetListenerTo)
        }

        onDateSetListenerTo = DatePickerDialog.OnDateSetListener { datePicker, year, month, day ->
            Log.e("333", "-datePicker=" + datePicker + "-year-" + year + "-month-" + month + "-day-" + day)

            // обращаемся к всопомгательному классу конвертации даты календаря
            val dateConverter = DateConverter()
            val objectConvertDate = ObjectConvertDate(year = year,month = month, day = day)
            // получаем из метода getDate строку с переработанной датой под сетевой запрос апи
            gamesViewModel.dataToString = dateConverter.getName(objectConvertDate)
            // получаем из метода getDate строку с переработанной датой для отображения в tvDateFrom
            bindingAlertDialogDate.tvDateTo.text = dateConverter.getName(objectConvertDate)

        }

        bindingAlertDialogDate.btCancel.setOnClickListener {
            dialog.dismiss()
            dialog.cancel()
        }
        bindingAlertDialogDate.btOk.setOnClickListener{
            dialog.dismiss()

            // удаляем из даты слева тире и запятую
            val stringFrom1 = gamesViewModel.dataFromString.replace("-","")
            val stringFrom2 = stringFrom1.replace(",","").toIntOrNull()
            // удаляем из даты справа тире
            val stringTo = gamesViewModel.dataToString.replace("-","").toIntOrNull()

            Log.e("333","=ДО stringFrom2="+ stringFrom2 + "=stringTo=" + stringTo)
            // проверка на то, что пользовтель ввел первую дату меньше чем вторую
            if ((stringFrom2!! - stringTo!!) <= 0) {
                    if(gamesViewModel.hashMapGames.value!!.size > 0) {
                        gamesViewModel.hashMapGames.value!!.clear()
                        gamesViewModel.hashMapTemp.clear()
                    }

                Log.e("333","=ПОСЛЕ stringFrom2="+ stringFrom2 + "=stringTo=" + stringTo)
                Log.e("333","=dataFromString="+ gamesViewModel.dataFromString + "=dataToString=" + gamesViewModel.dataToString)

                // https://api.rawg.io/api/games?key=YOUR_API_KEY&dates=2019-09-01,2019-09-30&platforms=18,1,7
                // вызов нового апи запроса в сеть для получения данных об играх
                gamesViewModel.getGames(apiKey, gamesViewModel.dataFromString as String,
                    gamesViewModel.dataToString as String, 1)

                (context as MainActivity?)?.runOnUiThread {
                    bindingFragmentGames.progressBar.visibility = View.VISIBLE
                }

            }else{
                val toast = Toast.makeText(activity, "The first date must be less than the second date", Toast.LENGTH_LONG)
                toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 0)
                toast.show()
            }
        }
    }


    private fun getCalendar(onDateSetListener: DatePickerDialog.OnDateSetListener?) {

        val calendar: Calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireActivity(),
            android.R.style.Theme_Holo_Dialog,
            onDateSetListener, year, month, day)
        datePickerDialog.window?.setBackgroundDrawable(ColorDrawable(R.drawable.gradient))
        datePickerDialog.show()


        datePickerDialog.setOnDismissListener {

            if(gamesViewModel.statusLeftDate) {
                gamesViewModel.statusLeftDate = false
            }
            if(gamesViewModel.statusRightDate) {
                gamesViewModel.statusRightDate = false
            }
        }
    }

//    // функция переработки полученной даты календаря (где нет нулей, там поставил)
//    fun convertDate(i: Int, i2: Int, i3: Int): String {
//        // i - год, i2 - месяц(январь с нуля идет отсчет), i3 - день
//        val i2 = i2 + 1 // + 1
//
//        // если месяц не входит в диапазон от 10 до 12, а день входит от 10 до 31
//        if (i2 !in 10..12 && i3 in 10..31) return String.format("$i" + "-" + "0$i2" + "-" + "$i3")
//        // если месяц входит в диапазон от 10 до 12, а день не входит от 10 до 31
//        if (i2 in 10..12 && i3 !in 10..31) return String.format("$i" + "-" + "$i2" + "-" + "0$i3")
//        // если месяц входит в диапазон от 10 до 12, и день входит от 10 до 31
//        if (i2 in 10..12 && i3 in 10..31) return String.format("$i" + "-" + "$i2" + "-" + "$i3")
//        // если месяц не входит в диапазон от 10 до 12, и день не входит от 10 до 31
//        if (i2 !in 10..12 && i3 !in 10..31) return String.format("$i" + "-" + "0$i2" + "-" + "0$i3")
//
//        return ""
//    }

}

//open class MyClass1{
//    open fun one(){
//        println("что печатает")
//    }
//
//}
//
//class MyClass2 : MyClass1() {
////    fun one(){
////        println("что печатает")
////    }
//
//}