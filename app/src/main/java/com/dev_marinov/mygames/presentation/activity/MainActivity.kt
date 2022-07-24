package com.dev_marinov.mygames.presentation.activity

import android.app.Dialog
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.dev_marinov.mygames.R
import com.dev_marinov.mygames.databinding.ActivityMainBinding
import com.dev_marinov.mygames.databinding.WindowsAlertdialogExitBinding


import android.view.LayoutInflater
import androidx.lifecycle.ViewModelProvider
import com.dev_marinov.mygames.presentation.games.GamesFragment


class MainActivity : AppCompatActivity() {

    private lateinit var bindingActivityMain: ActivityMainBinding
    lateinit var mainActivityViewModel: MainActivityViewModel

    private var mySavedInstanceState: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("333","=MainActivity=")

        bindingActivityMain = DataBindingUtil.setContentView(this, R.layout.activity_main)

        mainActivityViewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]

        mySavedInstanceState = savedInstanceState

        setWindow() // сетинг для статус бара и для бара навигации
        hideSystemUI() // сетинг для фул скрин по соответствующему сдк
        supportActionBar?.hide() // скрыть экшенбар

        // при создании макета проверяем статус был ли перед созданием макета открыт диалог
        // если да (true), значит запустим его снова
        if (mainActivityViewModel.status) {
            myAlertDialog()
        }

        setAnimAndTransaction()
    }

   private fun setAnimAndTransaction(){

       val runnable1 = Runnable{ // анимация шарики при старте
           bindingActivityMain.animationView.playAnimation()
       }
       Handler(Looper.getMainLooper()).postDelayed(runnable1, 0)
       bindingActivityMain.animationView.cancelAnimation()

       val runnable2 = Runnable{ // задержка 2 сек перед переходом во FragmentList
           if (mySavedInstanceState == null) {
               val fragmentList = GamesFragment()
               val fragmentManager = supportFragmentManager
               val fragmentTransaction = fragmentManager.beginTransaction()
               fragmentTransaction.add(R.id.llFragList, fragmentList)
               fragmentTransaction.commit()
           }
       }
       Handler(Looper.getMainLooper()).postDelayed(runnable2, 2000)
   }

    private fun setWindow() {
        val window = window
        // FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS Флаг, указывающий, что это Окно отвечает за отрисовку фона для системных полос.
        // Если установлено, системные панели отображаются с прозрачным фоном, а соответствующие области в этом окне заполняются
        // цветами, указанными в Window#getStatusBarColor()и Window#getNavigationBarColor().
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        window.statusBarColor = ContextCompat.getColor(this, android.R.color.transparent); // прозрачный статус бар
        window.navigationBarColor  = ContextCompat.getColor(this, android.R.color.black); // черный бар навигации
    }

    private fun hideSystemUI() {
        // если сдк устройства больше или равно сдк приложения
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.let {
                it.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else { // иначе
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                //or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                //or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                //or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                //or View.SYSTEM_UI_FLAG_FULLSCREEN
            )
        }
    }

    override fun onBackPressed() {
        // как только будет ноль (последний экран) выполниться else
        if (supportFragmentManager.backStackEntryCount > 0) {
            super.onBackPressed()
        } else {
            supportFragmentManager.popBackStack() // удаление фрагментов из транзакции
            myAlertDialog() // метод реализации диалога с пользователем закрыть приложение или нет
        }
    }

    private fun myAlertDialog() {
        val bindingAlertDialogExit: WindowsAlertdialogExitBinding = DataBindingUtil
            .inflate(LayoutInflater.from(this), R.layout.windows_alertdialog_exit, null, false)

        val dialog = Dialog(this)
        dialog.setContentView(bindingAlertDialogExit.root)
        dialog.setCancelable(true)
        dialog.show()

            // костыль для повторного открытия диалога если перевернули экран
        mainActivityViewModel.status = true
        dialog.setOnDismissListener {
            mainActivityViewModel.status = false
        }

        bindingAlertDialogExit.tvTitle.text = resources.getString(R.string.do_you_wish)
        bindingAlertDialogExit.btNo.text = resources.getString(R.string.no)
        bindingAlertDialogExit.btYes.text = resources.getString(R.string.yes)

        bindingAlertDialogExit.btNo.setOnClickListener {
            dialog.dismiss()
            dialog.cancel()
        }
        bindingAlertDialogExit.btYes.setOnClickListener{
            dialog.dismiss()
            finish()
        }
    }

}