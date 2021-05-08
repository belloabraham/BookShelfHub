package com.bookshelfhub.bookshelfhub.services.authentication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UserAuthViewModel : ViewModel() {

    private var isAuthenticated: MutableLiveData<Boolean>


    init {
      isAuthenticated  = MutableLiveData<Boolean>()
        getAuthenticationSate()
    }

     fun observeAuthState(): LiveData<Boolean>{
        return isAuthenticated
    }

     fun getAuthenticationSate(){
        //Get auth from firbase
        //set auth from firebase
        isAuthenticated.value = false
    }


}