package com.bookshelfhub.bookshelfhub.services.authentication

import androidx.lifecycle.LiveData

interface IPhoneAuthViewModel {
    fun setSignInCompleted(value: Boolean)
    fun getSignInCompleted(): LiveData<Boolean>
    fun setSignInStarted(value: Boolean)
    fun getSignInStarted(): LiveData<Boolean>
    fun setIsCodeSent(value: Boolean)
    fun getIsCodeSent(): LiveData<Boolean>
    fun setIsNewUser(isNewUser: Boolean?)
    fun getIsNewUser(): LiveData<Boolean>
    fun getIsSignedInSuccessfully(): LiveData<Boolean>
    fun setIsSignedInSuccessfully(value: Boolean)
    fun getIsSignedInFailedError(): LiveData<String>
    fun setSignedInFailedError(value: String)
    fun getOTPCode(): LiveData<String>
    fun setOTPCode(otpCode: String)
}