package com.bookshelfhub.bookshelfhub.ui.welcome

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bookshelfhub.bookshelfhub.Utils.TimerUtil
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class WelcomeActivityViewModel @Inject constructor(val timerUtil: TimerUtil) :ViewModel() {

    //private var isTimerCompleted: MutableLiveData<Boolean>
    private var timeRemainingInMillis: MutableLiveData<Long>
    private var timerStarted: Boolean = false
    private var otpCode: MutableLiveData<String>
    private var signedInFailedError: MutableLiveData<String>
    private var isVerificationCompleted: MutableLiveData<Boolean>
    private var isSignedInSuccessfully: MutableLiveData<Boolean>
    private var isCodeSent: MutableLiveData<Boolean>

    init{
        timeRemainingInMillis  = MutableLiveData<Long>()
        otpCode  = MutableLiveData<String>()
        isVerificationCompleted  = MutableLiveData<Boolean>()
        isSignedInSuccessfully  = MutableLiveData<Boolean>()
        signedInFailedError = MutableLiveData<String>()
        isCodeSent = MutableLiveData<Boolean>()
    }


    fun setIsCodeSent(value:Boolean){
        isCodeSent.value=value
    }
    fun getIsCodeSent():LiveData<Boolean>{
        return isCodeSent
    }

    //TODO How to confirm successful sign in
    fun getIsSignedInSuccessfully():LiveData<Boolean>{
        return isSignedInSuccessfully
    }
    fun setIsSignedInSuccessfully(value:Boolean){
        isSignedInSuccessfully.value=value
    }

    //TODO How to get sign in success or
    fun getIsSignedInFailedError():LiveData<String>{
        return signedInFailedError
    }
    fun setSignedInFailedError(value:String?){
        signedInFailedError.value=value
    }

    fun getTimerTimeRemaining():LiveData<Long>{
        return timeRemainingInMillis
    }

    fun getOTPCode():LiveData<String>{
        return otpCode
    }
    fun setOTPCode(otpCode: String){
        this.otpCode.value=otpCode
    }

    fun startTimer(length: Long){
        if (!timerStarted){
            timerStarted=true
            timerUtil.startTimer(length) {
                timeRemainingInMillis.value=it
            }

        }
    }
}