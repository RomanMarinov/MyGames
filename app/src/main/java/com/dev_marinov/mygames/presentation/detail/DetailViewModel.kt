package com.dev_marinov.mygames.presentation.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev_marinov.mygames.domain.IGameRepository
import com.dev_marinov.mygames.domain.detail.Detail
import com.dev_marinov.mygames.domain.screenshot.ScreenShotsImages
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(private val iGameRepository: IGameRepository) :
    ViewModel() {

    private var _detail: MutableLiveData<Detail> = MutableLiveData()
    var detail: LiveData<Detail> = _detail

    private var _screenShots: MutableLiveData<List<ScreenShotsImages>> = MutableLiveData()
    var screenShots: LiveData<List<ScreenShotsImages>> = _screenShots


    fun getDetail(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val detail = iGameRepository.getDetail(id = id)
            detail?.let {
                _detail.postValue(it)
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            val screenShot = iGameRepository.getScreenShot(id = id)
            screenShot?.let {
                _screenShots.postValue(it)
            }
        }
    }

}