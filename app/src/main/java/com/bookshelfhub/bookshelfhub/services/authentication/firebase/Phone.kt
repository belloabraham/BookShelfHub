package com.bookshelfhub.bookshelfhub.services.authentication.firebase

import android.app.Activity
import android.content.Context
import android.text.TextUtils
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.services.authentication.PhoneAuth
import com.bookshelfhub.bookshelfhub.ui.welcome.WelcomeActivityViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.qualifiers.ActivityContext
import java.util.concurrent.TimeUnit

open class Phone(private  val activity: Activity, val welcomeActViewModel: WelcomeActivityViewModel )
{

    private val auth: FirebaseAuth = Firebase.auth
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private val callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private var storedVerificationId: String? = null



    init {

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                welcomeActViewModel.setOTPCode("000000")
                signInWithPhoneAuthCredential(credential, welcomeActViewModel)
            }

            override fun onVerificationFailed(e: FirebaseException) {

                if (e is FirebaseAuthInvalidCredentialsException) {
                    welcomeActViewModel.setSignedInFailedError(activity.getString(R.string.otp_error_msg))
                } else if (e is FirebaseTooManyRequestsException) {
                    welcomeActViewModel.setSignedInFailedError(activity.getString(R.string.too_many_request_error))
                }else{
                    welcomeActViewModel.setSignedInFailedError(activity.getString(R.string.phone_sign_in_error))
                    //TODO Log error to Chrashlytics
                }
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                // Save verification ID and resending token so we can use them later
                storedVerificationId = verificationId
                resendToken = token
                welcomeActViewModel.setIsCodeSent(true)
            }
        }

    }

     open fun startPhoneNumberVerification(phoneNumber: String) {
         val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    open fun verifyPhoneNumberWithCode(code: String) {
            val credential = PhoneAuthProvider.getCredential(storedVerificationId!!, code)
            signInWithPhoneAuthCredential(credential, welcomeActViewModel)
    }


    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential, welcomeActViewModel:WelcomeActivityViewModel) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    welcomeActViewModel.setIsSignedInSuccessfully(user!=null)
                } else {
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        //TODO wrong verification code
                        welcomeActViewModel.setSignedInFailedError((task.exception as FirebaseAuthInvalidCredentialsException).message)
                    }
                }
            }
    }


    open fun resendVerificationCode(phoneNumber: String) {
        val optionsBuilder = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(activity)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
        if (resendToken != null) {
            optionsBuilder.setForceResendingToken(resendToken) // callback's ForceResendingToken
        }
        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
    }


}