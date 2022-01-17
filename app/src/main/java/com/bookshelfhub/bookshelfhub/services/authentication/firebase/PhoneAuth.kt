package com.bookshelfhub.bookshelfhub.services.authentication.firebase

import android.app.Activity
import com.bookshelfhub.bookshelfhub.services.authentication.IPhoneAuth
import com.bookshelfhub.bookshelfhub.services.authentication.PhoneAuthViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

open class PhoneAuth(private  val activity: Activity, val phoneAuthViewModel: PhoneAuthViewModel, wrongOTPErrorMsg:Int, tooManyReqErrorMsg:Int, otherAuthErrorMsg:Int) :
    IPhoneAuth {

    private val auth: FirebaseAuth = Firebase.auth
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private val callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private var storedVerificationId: String? = null

    init {
        callbacks = getAuthCallBack(wrongOTPErrorMsg, tooManyReqErrorMsg, otherAuthErrorMsg)
    }

     override fun startPhoneNumberVerification(phoneNumber: String) {
         val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    override fun verifyPhoneNumberWithCode(code: String, wrongOTPErrorMsg:Int) {
            val credential = PhoneAuthProvider.getCredential(storedVerificationId!!, code)
            signInWithPhoneAuthCredential(credential, phoneAuthViewModel, wrongOTPErrorMsg)
    }


    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential, phoneAuthViewModel: PhoneAuthViewModel,wrongOTPErrorMsg:Int) {
        phoneAuthViewModel.setSignInStarted(true)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    phoneAuthViewModel.setIsNewUser(task.result!!.additionalUserInfo!!.isNewUser)
                    phoneAuthViewModel.setIsSignedInSuccessfully(true)

                }else {
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        phoneAuthViewModel.setSignedInFailedError(activity.getString(wrongOTPErrorMsg))
                    }
                }
                phoneAuthViewModel.setSignInCompleted(true)
            }
    }


    override fun resendVerificationCode(phoneNumber: String) {
        val optionsBuilder = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(activity)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
        optionsBuilder.setForceResendingToken(resendToken)
        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
    }


    private fun getAuthCallBack(wrongOTPErrorMsg:Int, tooManyReqErrorMsg:Int, otherAuthErrorMsg:Int): PhoneAuthProvider.OnVerificationStateChangedCallbacks {
        return object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                phoneAuthViewModel.setOTPCode("000000")
                signInWithPhoneAuthCredential(credential, phoneAuthViewModel, wrongOTPErrorMsg)
            }

            override fun onVerificationFailed(e: FirebaseException) {

                when (e) {
                    is FirebaseAuthInvalidCredentialsException -> {
                        phoneAuthViewModel.setSignedInFailedError(activity.getString(wrongOTPErrorMsg))
                    }
                    is FirebaseTooManyRequestsException -> {
                        phoneAuthViewModel.setSignedInFailedError(activity.getString(tooManyReqErrorMsg))
                    }
                    else -> {
                        phoneAuthViewModel.setSignedInFailedError(activity.getString(otherAuthErrorMsg))
                    }
                }
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                // Save verification ID and resending token so we can use them later
                storedVerificationId = verificationId
                resendToken = token
                phoneAuthViewModel.setIsCodeSent(true)
            }
        }
    }

}