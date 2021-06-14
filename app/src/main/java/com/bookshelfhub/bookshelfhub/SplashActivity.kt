package com.bookshelfhub.bookshelfhub

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bookshelfhub.bookshelfhub.services.authentication.UserAuth
import com.bookshelfhub.bookshelfhub.services.database.local.LocalDb
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    @Inject
    lateinit var localDb: LocalDb
    @Inject
    lateinit var userAuth: UserAuth

    override fun onCreate(savedInstanceState: Bundle?) {
       hideSystemUI(window)
        super.onCreate(savedInstanceState)

        if (userAuth.getIsUserAuthenticated()){
            lifecycleScope.launch(IO) {
                val user = localDb.getUser()
              withContext(Main){
                 val intent = if (user.isPresent && userAuth.getUserId() == user.get().userId){
                      Intent(this@SplashActivity, MainActivity::class.java)
                  }else{
                      Intent(this@SplashActivity, WelcomeActivity::class.java)
                  }
                  finish()
                  startActivity(intent)
              }
            }
        }else{
             finish()
             startActivity(Intent(this, WelcomeActivity::class.java))
        }

    }

    fun hideSystemUI(window: Window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(true)
        }else{
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        }
    }

}