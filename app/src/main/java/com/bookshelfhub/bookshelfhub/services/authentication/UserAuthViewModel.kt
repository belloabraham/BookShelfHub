package com.bookshelfhub.bookshelfhub.services.authentication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserAuthViewModel @Inject constructor(): ViewModel(){
    private var isAddingUser: MutableLiveData<Boolean>  = MutableLiveData<Boolean>()
    private var isExistingUser: MutableLiveData<Boolean> = MutableLiveData<Boolean>()


    fun setIsExistingUser(value:Boolean){
        isExistingUser.value=value
    }

    fun getIsExistingUser(): LiveData<Boolean> {
        return isExistingUser
    }


    fun setIsAddingUser(value:Boolean){
        isAddingUser.value=value
    }

    fun getIsAddingUser(): LiveData<Boolean> {
        return isAddingUser
    }

}