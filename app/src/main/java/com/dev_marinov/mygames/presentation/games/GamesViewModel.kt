package com.dev_marinov.mygames.presentation.games

import androidx.lifecycle.*
import com.dev_marinov.mygames.SingleLiveEvent
import com.dev_marinov.mygames.domain.game.Game
import com.dev_marinov.mygames.domain.IGameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GamesViewModel @Inject constructor(
    private val iGameRepository: IGameRepository
) : ViewModel() {

    private val apiKey = "e0ecba986417447ebbaa87aad9d31458"
    private val platform = "18,1,7"
    var dataFromString = "2019-09-01,"
    var dataToString = "2019-09-30"
    var lastVisibleItemPosition = 0
    var page = 1

    var statusAlertDialogDate = false

    var statusLeftDate = false
    var statusRightDate = false

    private val limiter = 5

    private val _uploadData = SingleLiveEvent<String>()
    val uploadData: SingleLiveEvent<String> = _uploadData

    private val _games: MutableLiveData<List<Game>> = MutableLiveData()
    var games: LiveData<List<Game>> = _games

    private val _flagLoading: MutableLiveData<Boolean> = MutableLiveData()
    val flagLoading: LiveData<Boolean> = _flagLoading


    init {
        getGames(apiKey, dataFromString, dataToString, page, platform = platform)
    }

    fun clearGames(){
        _games.value = listOf()
    }

    fun onClick(id: String){
        _uploadData.postValue(id)
    }

    fun getGames(apiKey: String, dataFromString: String, dataToString: String, page: Int, platform: String) {
        _flagLoading.value = true

        viewModelScope.launch(Dispatchers.IO) {

            var list: MutableList<Game> = mutableListOf()
            _games.value?.let {
                list = it.toMutableList()
            }

            iGameRepository.getGames(
                apiKey = apiKey,
                dataFromString = dataFromString,
                dataToString = dataToString,
                page = page,
                platforms = platform)?.let {
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
            getGames(apiKey, dataFromString, dataToString, page, platform)
        }
    }

    private fun getMaxPosition(positions: IntArray): Int {
        return positions[positions.size-1]
    }

    fun onDataPickerFrom() {
        TODO("Not yet implemented")
    }

    fun onDateSelected(year: Int, month: Int, day: Int) {
        TODO("Not yet implemented")
    }

}