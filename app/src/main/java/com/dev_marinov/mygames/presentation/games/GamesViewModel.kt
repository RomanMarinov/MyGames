package com.dev_marinov.mygames.presentation.games

import android.util.Log
import androidx.lifecycle.*
import com.dev_marinov.mygames.SingleLiveEvent
import com.dev_marinov.mygames.data.ObjectConvertDate
import com.dev_marinov.mygames.domain.DateConverter
import com.dev_marinov.mygames.domain.game.Game
import com.dev_marinov.mygames.domain.IGameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GamesViewModel @Inject constructor(
    private val iGameRepository: IGameRepository
) : ViewModel() {

    private val dateConverter = DateConverter()

    private var page = 1
    private val limiter = 5
    private var lastVisibleItemPosition = 0

    var statusLeftDate = false
    var statusRightDate = false

    private val _uploadData = SingleLiveEvent<String>()
    val uploadData: SingleLiveEvent<String> = _uploadData

    private val _games: MutableLiveData<List<Game>> = MutableLiveData()
    var games: LiveData<List<Game>> = _games

    private val _dataFromString: MutableLiveData<String> = MutableLiveData("2019-09-01,")
    var dataFromString: LiveData<String> = _dataFromString

    private val _dataToString: MutableLiveData<String> = MutableLiveData("2019-09-30")
    var dataToString: LiveData<String> = _dataToString

    private val _flagLoading: MutableLiveData<Boolean> = MutableLiveData()
    val flagLoading: LiveData<Boolean> = _flagLoading

    private val _flagToast: MutableLiveData<Boolean> = MutableLiveData()
    val flagToast: LiveData<Boolean> = _flagToast

    private val _statusAlertDialogDate: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val statusAlertDialogDate: StateFlow<Boolean> = _statusAlertDialogDate


    init {
        getGames(dataFromString.value!!, dataToString.value!!, page)
    }

    fun onClick(id: String){
        _uploadData.postValue(id)
    }

    private fun getGames(dataFromString: String, dataToString: String, page: Int) {
        _flagLoading.value = true

        viewModelScope.launch(Dispatchers.IO) {

            var list: MutableList<Game> = mutableListOf()
            _games.value?.let {
                list = it.toMutableList()
            }

            iGameRepository.getGames(
                dataFromString = dataFromString,
                dataToString = dataToString,
                page = page)?.let {
                    list.addAll(it)
                    _games.postValue(list)
            }
            _flagLoading.postValue(false)
        }
    }

    fun onScroll(totalCountItem: Int, findLastVisibleItemPositions: IntArray) {
        lastVisibleItemPosition = getMaxPosition(findLastVisibleItemPositions)
        if (_flagLoading.value == false && (totalCountItem - limiter) == lastVisibleItemPosition) {
            page += 1
            getGames(dataFromString.value!!, dataToString.value!!, page)
        }
    }

    private fun getMaxPosition(positions: IntArray): Int {
        return positions[positions.size-1]
    }

    fun onDateSelectedFrom(year: Int, month: Int, day: Int) {
        val objectConvertDate = ObjectConvertDate(year = year,month = month, day = day)
        _dataFromString.value = dateConverter.getName(objectConvertDate) + ","
    }

    fun onDateSelectedTo(year: Int, month: Int, day: Int) {
        val objectConvertDate = ObjectConvertDate(year = year,month = month, day = day)
        // получаем из метода getDate строку с переработанной датой под сетевой запрос апи
        _dataToString.value = dateConverter.getName(objectConvertDate)
    }

    fun getGamesByCalendar() {
        // удаляем из даты слева тире и запятую
        val stringFrom1 = _dataFromString.value.toString()
        val stringFrom2 = stringFrom1.replace("-", "")
        val stringFrom3 = stringFrom2.replace(",", "").toIntOrNull()
        // удаляем из даты справа тире
        val stringTo1 = _dataToString.value.toString()
        val stringTo2 = stringTo1.replace("-", "").toIntOrNull()

        // проверка на то, что пользовтель ввел первую дату меньше чем вторую
        if ((stringFrom3!! - stringTo2!!) <= 0) {
            if (_games.value!!.isNotEmpty()) {
                _games.value = listOf()
            }

            getGames(_dataFromString.toString(), _dataToString.toString(), 1)
            _flagToast.value = false
        } else {
            _flagToast.value = true
        }
    }

    fun setStatusAlertDialogDate(b: Boolean) {
        _statusAlertDialogDate.value = b
    }

}