package com.bookshelfhub.bookshelfhub.domain.viewmodels

import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bookshelfhub.bookshelfhub.helpers.utils.AppUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserAuthViewModel @Inject constructor(private val appUtil: AppUtil): ViewModel(){
    private var isAddingUser: MutableLiveData<Boolean>  = MutableLiveData<Boolean>()
    private var isExistingUser: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    private var referrerId: String? =null


    fun getUserReferrerId():String?{
        return referrerId
    }
    fun setUserReferrerId(value:String?){
        referrerId=value
    }
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