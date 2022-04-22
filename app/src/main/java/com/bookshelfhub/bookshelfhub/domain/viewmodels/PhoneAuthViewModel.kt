package com.bookshelfhub.bookshelfhub.domain.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bookshelfhub.bookshelfhub.helpers.authentication.IPhoneAuthViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import kotlin.properties.Delegates


@HiltViewModel
class PhoneAuthViewModel @Inject constructor() :ViewModel(), IPhoneAuthViewModel {

    private var otpCode: MutableLiveData<String> = MutableLiveData<String>()
    private var signedInFailedError: MutableLiveData<String> = MutableLiveData<String>()
    private var isSignedInSuccessfully: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    private val _isCodeSentFlow = MutableSharedFlow<Boolean>()
    private var isCodeSentFlow = _isCodeSentFlow.asSharedFlow()

    private var isNewUser:Boolean?=null
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

    override suspend fun setIsCodeSent(value:Boolean){
        _isCodeSentFlow.emit(value)
    }
    override fun getIsCodeSent(): SharedFlow<Boolean> {
        return isCodeSentFlow
    }

    override fun setIsNewUser(isNewUser:Boolean?){
         this.isNewUser = isNewUser
    }

    override fun getIsNewUser(): Boolean?{
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