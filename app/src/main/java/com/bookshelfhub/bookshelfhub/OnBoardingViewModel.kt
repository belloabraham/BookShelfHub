package com.bookshelfhub.bookshelfhub

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.properties.Delegates

class OnBoardingViewModel: ViewModel() {

    private lateinit var phoneNumber: String
    private var isNewUser = true

    fun setPhoneNumber(phoneNumber:String){
        this.phoneNumber=phoneNumber
    }

    fun getPhoneNumber():String{
        return phoneNumber
    }

    fun getIsNewUser():Boolean{
        return isNewUser
    }

    fun setIsNewUser(isNewUser:Boolean){
        this.isNewUser=isNewUser
    }
}