package com.bookshelfhub.bookshelfhub.domain.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@HiltViewModel
class GoogleAuthViewModel @Inject constructor(): ViewModel(){
    private val _authenticationErrorFlow = MutableSharedFlow<String>()
    private var authenticationErrorSharedFlow = _authenticationErrorFlow.asSharedFlow()

    private var _signInErrorFlow = MutableSharedFlow<String>()
    private var signInErrorSharedFlow = _signInErrorFlow.asSharedFlow()

    private var isAuthenticationComplete: MutableLiveData<Boolean> = MutableLiveData<Boolean>()

    private var isNewUser:Boolean?=null
    private var isAuthenticationSuccessful: MutableLiveData<Boolean> = MutableLiveData<Boolean>()



    fun setIsNewUser(isNewUser:Boolean){
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

    fun getSignInError(): SharedFlow<String> {
        return signInErrorSharedFlow
    }

    suspend fun setSignInError(value:String){
        _signInErrorFlow.emit(value)
    }

    fun getAuthenticationError(): SharedFlow<String> {
        return authenticationErrorSharedFlow
    }

    suspend fun setAuthenticationError(value:String){
        _authenticationErrorFlow.emit(value)
    }

    fun setIsAuthenticationComplete(value:Boolean){
        isAuthenticationComplete.value=value
    }
    fun getIsAuthenticationComplete(): LiveData<Boolean> {
        return isAuthenticationComplete
    }

}