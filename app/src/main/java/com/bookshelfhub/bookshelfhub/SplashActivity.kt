package com.bookshelfhub.bookshelfhub

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bookshelfhub.bookshelfhub.services.authentication.UserAuthViewModel

class SplashActivity : AppCompatActivity() {

    private lateinit var userAuthViewModel: UserAuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userAuthViewModel =
            ViewModelProvider(this).get(UserAuthViewModel::class.java)

        userAuthViewModel.observeAuthState().observe(this, Observer { isAuth ->
            val intent: Intent
            if (isAuth == true){
                intent = Intent(this, MainActivity::class.java);
            }else{
                intent = Intent(this, WelcomeActivity::class.java);
            }
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        })
    }

}