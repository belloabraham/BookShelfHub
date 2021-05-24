package com.bookshelfhub.bookshelfhub.services.authentication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GoogleAuthViewModel @Inject constructor(): ViewModel(){
    private var authenticationError: MutableLiveData<String>
    private var isAuthenticationCompleted: MutableLiveData<Boolean>

    init {
        isAuthenticationCompleted = MutableLiveData<Boolean>()
        authenticationError = MutableLiveData<String>()
    }

    fun setIsAuthenticatedCompleted(value:Boolean){
        isAuthenticationCompleted.value=value
    }
    fun getIsAuthenticatedCompleted(): LiveData<Boolean> {
        return isAuthenticationCompleted
    }


    fun getAuthenticationError():LiveData<String>{
        return authenticationError
    }
    fun setAuthenticationError(value:String?){
        authenticationError.value=value
    }


}