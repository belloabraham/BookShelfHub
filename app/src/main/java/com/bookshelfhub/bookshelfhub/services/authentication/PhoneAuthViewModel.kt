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
class PhoneAuthViewModel @Inject constructor() :ViewModel(), IPhoneAuthViewModel {

    private var otpCode: MutableLiveData<String> = MutableLiveData<String>()
    private var signedInFailedError: MutableLiveData<String> = MutableLiveData<String>()
    private var isSignedInSuccessfully: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    private var isCodeSent: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    private var isNewUser:MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    private var signInStarted: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    private var signInCompleted: MutableLiveData<Boolean> = MutableLiveData<Boolean>()


    override fun setSignInCompleted(value:Boolean){
        signInCompleted.value=value
    }
    override fun getSignInCompleted():LiveData<Boolean>{
        return signInCompleted
    }

    override fun setSignInStarted(value:Boolean){
        signInStarted.value=value
    }
    override fun getSignInStarted():LiveData<Boolean>{
        return signInStarted
    }

    override fun setIsCodeSent(value:Boolean){
        isCodeSent.value=value
    }
    override fun getIsCodeSent():LiveData<Boolean>{
        return isCodeSent
    }

    override fun setIsNewUser(isNewUser:Boolean?){
         this.isNewUser.value = isNewUser
    }

    override fun getIsNewUser():LiveData<Boolean>{
        return isNewUser
    }

    override fun getIsSignedInSuccessfully():LiveData<Boolean>{
        return isSignedInSuccessfully
    }
    override fun setIsSignedInSuccessfully(value:Boolean){
        isSignedInSuccessfully.value=value
    }

    override fun getIsSignedInFailedError():LiveData<String>{
        return signedInFailedError
    }

    override fun setSignedInFailedError(value:String){
        signedInFailedError.value=value
    }

    override fun getOTPCode():LiveData<String>{
        return otpCode
    }
    override fun setOTPCode(otpCode: String){
        this.otpCode.value=otpCode
    }

}