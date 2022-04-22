package com.bookshelfhub.bookshelfhub.helpers.authentication.firebase

import android.app.Activity
import com.bookshelfhub.bookshelfhub.helpers.authentication.IPhoneAuth
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

open class PhoneAuth(private  val activity: Activity) :
    IPhoneAuth {

    private val auth: FirebaseAuth = Firebase.auth

     override fun sendVerificationCode(phoneNumber: String, callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks) {
         val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
             .setActivity(activity)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    override fun verifyPhoneNumberWithCode(code: String, storedVerificationId: String): Task<AuthResult> {
            val credential = PhoneAuthProvider.getCredential(storedVerificationId, code)
        return auth.signInWithCredential(credential)
    }

    override fun signInWithCredential(credential:PhoneAuthCredential){
        auth.signInWithCredential(credential)
    }

    override fun resendVerificationCode(phoneNumber: String, resendToken: PhoneAuthProvider.ForceResendingToken, callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks) {
        val optionsBuilder = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(activity)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
        optionsBuilder.setForceResendingToken(resendToken)
        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
    }


}