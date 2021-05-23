package com.bookshelfhub.bookshelfhub

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.bookshelfhub.bookshelfhub.services.authentication.UserAuth
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
       hideSystemUI(window)
        super.onCreate(savedInstanceState)

        val intent: Intent
        intent = if (UserAuth().getIsUserAuthenticated()){
            Intent(this, MainActivity::class.java);
        }else{
            Intent(this, WelcomeActivity::class.java);
        }
        finish()
        startActivity(intent)

    }

    fun hideSystemUI(window: Window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(true)
        }else{
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        }
    }

}