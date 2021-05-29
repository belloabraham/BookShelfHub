package com.bookshelfhub.bookshelfhub

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.bookshelfhub.bookshelfhub.services.authentication.UserAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {


    @Inject
    lateinit var userAuth: UserAuth

    override fun onCreate(savedInstanceState: Bundle?) {
       hideSystemUI(window)
        super.onCreate(savedInstanceState)

        val intent: Intent
        if (userAuth.getIsUserAuthenticated()){
            //TODO get user data from database using user ID if user data exist then navigate straight to mainactivity
                 if (true){
                     intent = Intent(this, MainActivity::class.java);
                 }else{
                     intent = Intent(this, WelcomeActivity::class.java);
                 }
        }else{
           intent = Intent(this, WelcomeActivity::class.java);
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