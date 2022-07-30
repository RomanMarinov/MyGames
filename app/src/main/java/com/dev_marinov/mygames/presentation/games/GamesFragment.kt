package com.dev_marinov.mygames.presentation.games

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.res.Configuration
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.dev_marinov.mygames.R
import com.dev_marinov.mygames.databinding.WindowsAlertdialogBinding
import com.dev_marinov.mygames.databinding.FragmentGamesBinding
import dagger.hilt.android.AndroidEntryPoint
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData

@AndroidEntryPoint
class GamesFragment : Fragment() {

    private lateinit var binding: FragmentGamesBinding
    private lateinit var bindingAlertDialogDate: WindowsAlertdialogBinding

    private val viewModel: GamesViewModel by viewModels()

    private var onDateSetListenerFrom: DatePickerDialog.OnDateSetListener? = null // слушатель даты слева
    private var onDateSetListenerTo: DatePickerDialog.OnDateSetListener? = null // слушатель даты справа
    lateinit var dialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return initInterFace(inflater, container)
    }

    private fun initInterFace(inflater: LayoutInflater, container: ViewGroup?): View {
        container?.let { it.removeAllViewsInLayout() }

        val orientation = requireActivity().resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            binding = DataBindingUtil.inflate(inflater, R.layout.fragment_games, container, false)
        } else {
            binding = DataBindingUtil.inflate(inflater, R.layout.fragment_games, container, false)
        }

        setBindingDialogDate(inflater, container)
        createdDialog()
        setLayout()
        checkStatusAlertDialogDate()

        return binding.root
    }

    private fun setBindingDialogDate(inflater: LayoutInflater, container: ViewGroup?) {
        bindingAlertDialogDate = DataBindingUtil
            .inflate(inflater, R.layout.windows_alertdialog, container, false)
    }

    private fun createdDialog(){
        dialog = Dialog(requireActivity())
        dialog.setContentView(bindingAlertDialogDate.root)
        dialog.setCancelable(true) // чтобы можно было просто кликнув полю сбросить окно
        // установка ширины диалога, а то больше ничем не изменить ширину диалога
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
//        dialog.show()
        dialog.window!!.attributes = lp
    }

    private fun setLayout() {


        val gamesAdapter = GamesAdapter(viewModel::onClick)
        val staggeredGridLayoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.recyclerView.apply {
            setHasFixedSize(false)
            layoutManager = staggeredGridLayoutManager
            adapter = gamesAdapter
        }

        viewModel.games.observe(viewLifecycleOwner) {
            it?.let { gamesAdapter.submitList(it) }
        }

        viewModel.uploadData.observe(viewLifecycleOwner) {
            navigateToDetailFragment(it)
        }

        binding.btSetRangeDate.setOnClickListener { // кнопка выбора/установка диапазона дат для получения новых данных
            setAlertDialogRangeDate()
        }

        viewModel.flagLoading.observe(viewLifecycleOwner) {
            binding.progressBar.isVisible = it
        }

        viewModel.flagToast.observe(viewLifecycleOwner) {
            if (it == true) {
                val toast = Toast.makeText(activity, "The first date must be less than the second date", Toast.LENGTH_LONG)
                toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 0)
                toast.show()
            }
        }

        viewModel.dataFromString.observe(viewLifecycleOwner) {
            // отображаем слева в textview например "2019-09-01," только без запятой, т.е. "2019-09-01"
            bindingAlertDialogDate.tvDateFrom.text = it.replace(",", "")
        }

        viewModel.dataToString.observe(viewLifecycleOwner) {
            // установка даты для отображения в tvDateTo
            bindingAlertDialogDate.tvDateTo.text = it
        }

        val scrollListener: RecyclerView.OnScrollListener =
            object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    viewModel.onScroll(
                        staggeredGridLayoutManager.itemCount,
                        staggeredGridLayoutManager.findLastVisibleItemPositions(null)
                    )
                }
            }
        binding.recyclerView.addOnScrollListener(scrollListener)
    }

    private fun checkStatusAlertDialogDate() {
        // при создании макета проверяем статус был ли перед созданием макета открыт диалог
        // если да (true), значит запустим его снова

        viewModel.statusAlertDialogDate.asLiveData().observe(viewLifecycleOwner){
            Log.e("333","=it=" + it)
            if(it) {
                setAlertDialogRangeDate()

                if (viewModel.statusLeftDate) {
                    getCalendar(onDateSetListenerFrom) // запускаем левый календарь
                }
                if (viewModel.statusRightDate) {
                    getCalendar(onDateSetListenerTo) // запускаем правый календарь
                }
            }
        }

    }

    private fun setAlertDialogRangeDate() { // установка дат

        dialog.show()

        viewModel.setStatusAlertDialogDate(true)
        dialog.setOnDismissListener {
            viewModel.setStatusAlertDialogDate(false)
        }

        bindingAlertDialogDate.tvDateFrom.setOnClickListener {

            viewModel.statusLeftDate = true
            getCalendar(onDateSetListenerFrom)
        }
        onDateSetListenerFrom = DatePickerDialog.OnDateSetListener { datePicker, year, month, day ->
            viewModel.onDateSelectedFrom(year, month, day)
        }

        bindingAlertDialogDate.tvDateTo.setOnClickListener {

            viewModel.statusRightDate = true
            getCalendar(onDateSetListenerTo)
        }
        onDateSetListenerTo = DatePickerDialog.OnDateSetListener { datePicker, year, month, day ->
            viewModel.onDateSelectedTo(year, month, day)
        }

        bindingAlertDialogDate.btCancel.setOnClickListener {
            dialog.dismiss()
            dialog.cancel()
        }
        bindingAlertDialogDate.btOk.setOnClickListener {
            dialog.dismiss()
            viewModel.getGamesByCalendar()
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
            onDateSetListener, year, month, day
        )
        datePickerDialog.window?.setBackgroundDrawable(ColorDrawable(R.drawable.gradient))
        datePickerDialog.show()

        // ИСПРАВИТЬ
        datePickerDialog.setOnDismissListener {

            if (viewModel.statusLeftDate) {
                viewModel.statusLeftDate = false
            }
            if (viewModel.statusRightDate) {
                viewModel.statusRightDate = false
            }
        }
    }

    private fun navigateToDetailFragment(id: String) {
        val action = GamesFragmentDirections.actionGamesFragmentToDetailFragment(id)
        findNavController().navigate(action)
    }

}