package com.bookshelfhub.bookshelfhub.services.authentication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GoogleAuthViewModel @Inject constructor(): ViewModel(){
    private var authenticationError: MutableLiveData<String> = MutableLiveData<String>()
    private var isAuthenticationComplete: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    private var signInError: MutableLiveData<String> = MutableLiveData<String>()
    private var isNewUser:MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    private var isAuthenticationSuccessful: MutableLiveData<Boolean> = MutableLiveData<Boolean>()


    fun setIsNewUser(isNewUser:Boolean){
        this.isNewUser.value = isNewUser
    }

    fun getIsNewUser():LiveData<Boolean>{
        return isNewUser
    }

    fun setIsAuthenticatedSuccessful(value:Boolean){
        isAuthenticationSuccessful.value=value
    }
    fun getIsAuthenticatedSuccessful(): LiveData<Boolean> {
        return isAuthenticationSuccessful
    }

    fun getSignInError():LiveData<String>{
        return signInError
    }
    fun setSignInError(value:String){
        signInError.value=value
    }

    fun getAuthenticationError():LiveData<String>{
        return authenticationError
    }
    fun setAuthenticationError(value:String){
        authenticationError.value=value
    }

    fun setIsAuthenticationComplete(value:Boolean){
        isAuthenticationComplete.value=value
    }
    fun getIsAuthenticationComplete(): LiveData<Boolean> {
        return isAuthenticationComplete
    }

}