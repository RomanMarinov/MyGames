package com.dev_marinov.mygames.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dev_marinov.mygames.data.ObjectListDetail

class SharedViewModel : ViewModel() {

    val message: MutableLiveData<HashMap<Int, ObjectListDetail>> = MutableLiveData()

    fun sendMessage(myHashMapDetail: HashMap<Int, ObjectListDetail>) {
        message.value = myHashMapDetail
    }
}