package com.dev_marinov.mygames.presentation.games

import android.util.Log
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

    var flagLoading = false
    var dataFromString = "2019-09-01,"
    var dataToString = "2019-09-30"
    var lastVisibleItemPosition = 0
    var page = 1

    var statusAlertDialogDate = false
    var statusLeftDate = false
    var statusRightDate = false

    var totalCountItem = 0

    private val _uploadData = SingleLiveEvent<String>()
    val uploadData: SingleLiveEvent<String> = _uploadData

    private val _games: MutableLiveData<List<Game>> = MutableLiveData()
    var games: LiveData<List<Game>> = _games

    init {
        getGames(apiKey, dataFromString, dataToString, page, platform = platform)
    }

    // https://api.rawg.io/api/games?key=YOUR_API_KEY&dates=2019-09-01,2019-09-30&platforms=18,1,7

    fun clearGames(){
        _games.value = listOf()
    }

    fun onClick(id: String){
        _uploadData.postValue(id)
    }

    fun getGames(apiKey: String, dataFromString: String, dataToString: String, page: Int, platform: String) {
        Log.e("333", "=getGames=")

        viewModelScope.launch(Dispatchers.IO) {
            iGameRepository.getGames(
                apiKey = apiKey,
                dataFromString = dataFromString,
                dataToString = dataToString,
                page = page,
                platforms = platform)?.let {
                    _games.postValue(it)
            }
        }
    }

}