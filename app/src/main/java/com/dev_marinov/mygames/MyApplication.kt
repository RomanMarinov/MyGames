package com.dev_marinov.mygames

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application (){

//    // первая функция, которая запускаяется при старте приложения
//
//    // здесь будет инициализироваться наш класс retroComponent
//    private lateinit var retroComponent: RetroComponent
//
//    override fun onCreate() {
//        super.onCreate()
//
//        // т.к. DaggerRetroComponent создается автоматически, поэтому он должен быть создан во ремя компиляции
//        retroComponent = DaggerRetroComponent.builder().retroModule(RetroModule()).build()
//    }
//
//    fun getRetroComponent(): RetroComponent {
//        return retroComponent
//    }
}