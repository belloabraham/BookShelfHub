package com.bookshelfhub.bookshelfhub

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.bookshelfhub.bookshelfhub.databinding.ActivityWelcomeBinding
import com.bookshelfhub.bookshelfhub.services.authentication.PhoneAuth
import com.bookshelfhub.bookshelfhub.ui.welcome.WelcomeActivityViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WelcomeActivity : AppCompatActivity() {

    private lateinit var layout: ActivityWelcomeBinding
    private lateinit var phoneAuth:PhoneAuth
    private val welcomeActivityViewModel: WelcomeActivityViewModel by viewModels()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layout = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(layout.root)

        phoneAuth= PhoneAuth(this, welcomeActivityViewModel)

        welcomeActivityViewModel.getIsCodeSent().observe(this, Observer { isCodeSent ->
                layout.lottieContainerView.visibility = View.GONE
                layout.lottieAnimView.cancelAnimation()
        })

        welcomeActivityViewModel.getIsSignedInSuccessfully().observe(this, Observer { isSignedInSuccessfully ->
            if (isSignedInSuccessfully){
             val intent = Intent(this, MainActivity::class.java);
                finish()
                startActivity(intent)
            }
        })

    }


    fun startPhoneNumberVerification(phoneNumber: String){
        layout.lottieContainerView.visibility = View.VISIBLE
        layout.lottieAnimView.playAnimation()
        phoneAuth.startPhoneNumberVerification(phoneNumber)
    }

    fun resendVerificationCode(number:String){
        layout.lottieContainerView.visibility = View.VISIBLE
        layout.lottieAnimView.playAnimation()
       phoneAuth.resendVerificationCode(number)
    }

    fun verifyPhoneNumberWithCode(code:String){
        phoneAuth.verifyPhoneNumberWithCode(code)
    }

    override fun onBackPressed() {
        if (!layout.lottieAnimView.isAnimating){
            super.onBackPressed()
        }
    }

}