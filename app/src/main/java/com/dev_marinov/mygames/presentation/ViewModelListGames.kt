package com.dev_marinov.mygames.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dev_marinov.mygames.data.ObjectListGames
import com.dev_marinov.mygames.model.RequestData

class ViewModelListGames : ViewModel() {

    private var hashMapGames: MutableLiveData<HashMap<Int, ObjectListGames>> = MutableLiveData()

    //инициализируем список и заполняем его данными пользователей
    init {
        // с помощью value можно получить и отправить данные любым активным подписчикам
        // RequestData.getData()
        // RequestData.getData(dataFromString as String, dataToString as String, 1)
        hashMapGames.value = RequestData.getHashMapGames()
    }

    fun getHashMapGames() = hashMapGames

    fun setParams(
        dataFromString: String,
        dataToString: String,
        page: Int,
        viewModelFlagLoading: ViewModelFlagLoading
    ) {
        RequestData.getData(dataFromString, dataToString, page, viewModelFlagLoading)
    }

}