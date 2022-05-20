package com.dev_marinov.mygames.presentation

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
import android.widget.Button
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieAnimationView
import com.dev_marinov.mygames.data.ObjectListGames
import com.dev_marinov.mygames.R
import com.dev_marinov.mygames.data.ObjectListDetail


class MainActivity : AppCompatActivity() {

    lateinit var btNo: Button
    lateinit var btYes: Button
    var animationView: LottieAnimationView? = null // анимация на старте

    var mySavedInstanceState: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.e("333","=MainActivity=")

        mySavedInstanceState = savedInstanceState

        setWindow() // сетинг для статус бара и для бара навигации
        hideSystemUI() // сетинг для фул скрин по соответствующему сдк
        supportActionBar?.hide() // скрыть экшенбар
        animationView = findViewById(R.id.animationView)

        setAnimAndTransaction()
    }

   fun setAnimAndTransaction(){

       val runnable1 = Runnable{ // анимация шарики при старте
           animationView?.playAnimation()
       }
       Handler(Looper.getMainLooper()).postDelayed(runnable1, 0)
       animationView?.cancelAnimation()

       val runnable2 = Runnable{ // задержка 2 сек перед переходом во FragmentList
           if (mySavedInstanceState == null) {
               val fragmentList = FragmentList()
               val fragmentManager = supportFragmentManager
               val fragmentTransaction = fragmentManager.beginTransaction()
               fragmentTransaction.add(R.id.llFragList, fragmentList)
               fragmentTransaction.commit()
           }

       }
       Handler(Looper.getMainLooper()).postDelayed(runnable2, 2000)
   }

    fun setWindow() {
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

    fun myAlertDialog() {
        val dialog = Dialog(this@MainActivity)
        dialog.setContentView(R.layout.windows_alertdialog_exit)
        dialog.setCancelable(false)
        dialog.show()

        btNo = dialog.findViewById(R.id.btNo)
        btYes = dialog.findViewById(R.id.btYes)

        btNo.setOnClickListener {
            dialog.dismiss()
            dialog.cancel()
        }
        btYes.setOnClickListener{
            dialog.dismiss()
            finish()
        }
    }



    companion object{
        lateinit var myInterFaceGames: MyInterFaceGames
        lateinit var myInterFaceDetail: MyInterFaceDetail
    }

    // интерфейс для работы с FragmentGames
    interface MyInterFaceGames{
        fun methodMyInterFaceGames()
    }
    fun setMyInterFaceGames(myInterFaceGames: MyInterFaceGames) {
        Companion.myInterFaceGames = myInterFaceGames
    }

    // интерфейс для работы с деталями
    interface MyInterFaceDetail{
        fun methodMyInterFaceDetail(hashMapDetail: HashMap<Int, ObjectListDetail>)
    }
    fun setMyInterFaceDetail(myInterFaceDetail: MyInterFaceDetail) {
        Companion.myInterFaceDetail = myInterFaceDetail
    }

}