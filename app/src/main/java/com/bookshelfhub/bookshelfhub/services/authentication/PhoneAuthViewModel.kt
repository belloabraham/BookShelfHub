package com.bookshelfhub.bookshelfhub.services.authentication

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bookshelfhub.bookshelfhub.Utils.TimerUtil
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class PhoneAuthViewModel @Inject constructor() :ViewModel() {

    private var otpCode: MutableLiveData<String> = MutableLiveData<String>()
    private var signedInFailedError: MutableLiveData<String> = MutableLiveData<String>()
    private var isSignedInSuccessfully: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    private var isCodeSent: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    private var isNewUser:MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    private var signInStarted: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    private var signInCompleted: MutableLiveData<Boolean> = MutableLiveData<Boolean>()


    fun setSignInCompleted(value:Boolean){
        signInCompleted.value=value
    }
    fun getSignInCompleted():LiveData<Boolean>{
        return signInCompleted
    }

    fun setSignInStarted(value:Boolean){
        signInStarted.value=value
    }
    fun getSignInStarted():LiveData<Boolean>{
        return signInStarted
    }

    fun setIsCodeSent(value:Boolean){
        isCodeSent.value=value
    }
    fun getIsCodeSent():LiveData<Boolean>{
        return isCodeSent
    }

    fun setIsNewUser(isNewUser:Boolean?){
         this.isNewUser.value = isNewUser
    }

    fun getIsNewUser():LiveData<Boolean>{
        return isNewUser
    }

    fun getIsSignedInSuccessfully():LiveData<Boolean>{
        return isSignedInSuccessfully
    }
    fun setIsSignedInSuccessfully(value:Boolean){
        isSignedInSuccessfully.value=value
    }

    fun getIsSignedInFailedError():LiveData<String>{
        return signedInFailedError
    }

    fun setSignedInFailedError(value:String){
        signedInFailedError.value=value
    }

    fun getOTPCode():LiveData<String>{
        return otpCode
    }
    fun setOTPCode(otpCode: String){
        this.otpCode.value=otpCode
    }

}