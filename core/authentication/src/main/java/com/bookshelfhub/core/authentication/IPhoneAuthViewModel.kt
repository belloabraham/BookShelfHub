package com.bookshelfhub.core.authentication

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.SharedFlow

interface IPhoneAuthViewModel {
    fun setSignInCompleted(value: Boolean)
    fun getSignInCompleted(): LiveData<Boolean>
    fun setSignInStarted(value: Boolean)
    fun getSignInStarted(): LiveData<Boolean>
    suspend fun setIsCodeSent(value: Boolean)
    fun getIsCodeSent(): SharedFlow<Boolean>
    fun setIsNewUser(isNewUser: Boolean?)
    fun getIsNewUser(): Boolean?
    fun getIsSignedInSuccessfully(): LiveData<Boolean>
    fun setIsSignedInSuccessfully(value: Boolean)
    fun getIsSignedInFailedError(): LiveData<String>
    fun setSignedInFailedError(value: String)
    fun getOTPCode(): LiveData<String>
    fun setOTPCode(otpCode: String)
}