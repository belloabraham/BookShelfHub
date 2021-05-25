package com.bookshelfhub.bookshelfhub.services.authentication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GoogleAuthViewModel @Inject constructor(): ViewModel(){
    private var authenticationError: MutableLiveData<String>
    private var isAuthenticationComplete: MutableLiveData<Boolean>
    private var signInError: MutableLiveData<String>
    private var isNewUser:Boolean? = null
    private var isAuthenticationSuccessful: MutableLiveData<Boolean>

    init {
        isAuthenticationComplete = MutableLiveData<Boolean>()
        isAuthenticationSuccessful = MutableLiveData<Boolean>()
        authenticationError = MutableLiveData<String>()
        signInError = MutableLiveData<String>()
    }

    fun setIsNewUser(isNewUser:Boolean?){
        this.isNewUser = isNewUser
    }

    fun getIsNewUser():Boolean?{
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
    fun setSignInError(value:String?){
        signInError.value=value
    }

    fun getAuthenticationError():LiveData<String>{
        return authenticationError
    }
    fun setAuthenticationError(value:String?){
        authenticationError.value=value
    }

    fun setIsAuthenticationdComplete(value:Boolean){
        isAuthenticationComplete.value=value
    }
    fun getIsAuthenticationdComplete(): LiveData<Boolean> {
        return isAuthenticationComplete
    }

}