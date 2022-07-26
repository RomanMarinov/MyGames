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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.dev_marinov.mygames.R
import com.dev_marinov.mygames.databinding.WindowsAlertdialogBinding
import com.dev_marinov.mygames.data.ObjectConvertDate
import com.dev_marinov.mygames.domain.DateConverter
import com.dev_marinov.mygames.databinding.FragmentGamesBinding
import com.dev_marinov.mygames.presentation.activity.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GamesFragment : Fragment() {

    val apiKey = "e0ecba986417447ebbaa87aad9d31458"
    private lateinit var bindingFragmentGames: FragmentGamesBinding
    private val platform = "18,1,7"
    lateinit var viewModel: GamesViewModel

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

        viewModel = ViewModelProvider(this)[GamesViewModel::class.java]

        checkStatusAlertDialogDate(container)
        bindingFragmentGames.progressBar.visibility = View.VISIBLE

        val gamesAdapter = GamesAdapter(viewModel::onClick)
        val staggeredGridLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        bindingFragmentGames.recyclerView.apply {
            setHasFixedSize(false)
            layoutManager = staggeredGridLayoutManager
            adapter = gamesAdapter
        }

        viewModel.games.observe(viewLifecycleOwner) {
            bindingFragmentGames.progressBar.visibility = View.GONE
            viewModel.flagLoading = false
            it?.let {
                gamesAdapter.submitList(it)
            }
        }

        viewModel.uploadData.observe(viewLifecycleOwner){
            navigateToDetailFragment(it)
        }

        bindingFragmentGames.btSetRangeDate.setOnClickListener { // кнопка выбора/установка диапазона дат для получения новых данных
            myAlertDialogMain(container)
        }

        // второй слушатель RecyclerView реализации offset чтобы подгружать данные при скроле
        val scrollListener: RecyclerView.OnScrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                // эта часть отвечает за срабатывание запроса на получение дополнительных данных для записи в hashMap
                // totalCountItem переменная всегда равно размеру hashmap в который добавляется + 20
                viewModel.totalCountItem = staggeredGridLayoutManager.itemCount

                // эта часть отвечет только за передачу последнего видимомо элемента
               val lastVisibleItemArrayPositions2 = staggeredGridLayoutManager.findLastVisibleItemPositions(null)
                viewModel.lastVisibleItemPosition = getMaxPosition(lastVisibleItemArrayPositions2!!)

                if (viewModel.flagLoading == false
                    && (viewModel.totalCountItem - 5) ==  viewModel.lastVisibleItemPosition) {
                    // тут я запускаю новый запрос даных на сервер с offset
                    val runnable = Runnable {
                        viewModel.page = viewModel.page + 1// переменная для увеличения значения offset

                        viewModel.getGames(apiKey, viewModel.dataFromString,
                            viewModel.dataToString, viewModel.page, platform)
                    }
                    Handler(Looper.getMainLooper()).postDelayed(runnable, 100)

                    (context as MainActivity?)?.runOnUiThread {
                        bindingFragmentGames.progressBar.visibility = View.VISIBLE
                    }

                    viewModel.flagLoading = true // и возвращаю flagLoading в исходное состояние
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
        if(viewModel.statusAlertDialogDate) {
            // обращаемся к вспомогательному классу диалога для запуска
            myAlertDialogMain(container)
            if(viewModel.statusLeftDate) {
                getCalendar(onDateSetListenerFrom) // запускаем левый календарь
            }
            if(viewModel.statusRightDate) {
                getCalendar(onDateSetListenerTo) // запускаем правый календарь
            }
        }
    }

    private fun navigateToDetailFragment(id: String) {
        val action = GamesFragmentDirections.actionGamesFragmentToDetailFragment(id)
        findNavController().navigate(action)
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
        viewModel.statusAlertDialogDate = true
        dialog.setOnDismissListener {
            Log.e("333", "-dialog.setOnDismissListener=")
            viewModel.statusAlertDialogDate = false
        }

        // отображаем слева в textview например "2019-09-01," только без запятой, т.е. "2019-09-01"
        bindingAlertDialogDate.tvDateFrom.text = String.format(viewModel.dataFromString)
            .replace(",","")

        // клик на textview слева
        bindingAlertDialogDate.tvDateFrom.setOnClickListener {
            viewModel.statusLeftDate = true
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
            viewModel.dataFromString = dateConverter.getName(objectConvertDate) + ","
            // получаем из метода getDate строку с переработанной датой для отображения в tvDateFrom
            bindingAlertDialogDate.tvDateFrom.text = dateConverter.getName(objectConvertDate)
        }

            // установка даты для отображения в tvDateTo
        bindingAlertDialogDate.tvDateTo.text = String.format(viewModel.dataToString)

        bindingAlertDialogDate.tvDateTo.setOnClickListener {
            viewModel.statusRightDate = true
            getCalendar(onDateSetListenerTo)
        }

        onDateSetListenerTo = DatePickerDialog.OnDateSetListener { datePicker, year, month, day ->
            Log.e("333", "-datePicker=" + datePicker + "-year-" + year + "-month-" + month + "-day-" + day)

            // обращаемся к всопомгательному классу конвертации даты календаря
            val dateConverter = DateConverter()
            val objectConvertDate = ObjectConvertDate(year = year,month = month, day = day)
            // получаем из метода getDate строку с переработанной датой под сетевой запрос апи
            viewModel.dataToString = dateConverter.getName(objectConvertDate)
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
            val stringFrom1 = viewModel.dataFromString.replace("-","")
            val stringFrom2 = stringFrom1.replace(",","").toIntOrNull()
            // удаляем из даты справа тире
            val stringTo = viewModel.dataToString.replace("-","").toIntOrNull()

            Log.e("333","=ДО stringFrom2="+ stringFrom2 + "=stringTo=" + stringTo)
            // проверка на то, что пользовтель ввел первую дату меньше чем вторую
            if ((stringFrom2!! - stringTo!!) <= 0) {
                    if(viewModel.games.value!!.size > 0) {
                        viewModel.clearGames()
                    }

                Log.e("333","=ПОСЛЕ stringFrom2="+ stringFrom2 + "=stringTo=" + stringTo)
                Log.e("333","=dataFromString="+ viewModel.dataFromString + "=dataToString=" + viewModel.dataToString)

                // https://api.rawg.io/api/games?key=YOUR_API_KEY&dates=2019-09-01,2019-09-30&platforms=18,1,7
                // вызов нового апи запроса в сеть для получения данных об играх
                viewModel.getGames(apiKey, viewModel.dataFromString as String,
                    viewModel.dataToString as String, 1, platform)

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

            if(viewModel.statusLeftDate) {
                viewModel.statusLeftDate = false
            }
            if(viewModel.statusRightDate) {
                viewModel.statusRightDate = false
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