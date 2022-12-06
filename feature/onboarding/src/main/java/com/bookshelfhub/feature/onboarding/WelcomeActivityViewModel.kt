package com.bookshelfhub.feature.onboarding

import android.os.Build
import androidx.lifecycle.*
import com.bookshelfhub.core.authentication.Auth
import com.bookshelfhub.core.authentication.IUserAuth
import com.bookshelfhub.core.common.helpers.ErrorUtil
import com.bookshelfhub.core.common.helpers.utils.AppUtil
import com.bookshelfhub.core.common.helpers.utils.DeviceUtil
import com.bookshelfhub.core.data.Payment
import com.bookshelfhub.core.data.repos.bookinterest.IBookInterestRepo
import com.bookshelfhub.core.data.repos.user.IUserRepo
import com.bookshelfhub.core.dynamic_link.Referrer
import com.bookshelfhub.core.model.entities.BookInterest
import com.bookshelfhub.core.model.entities.User
import com.bookshelfhub.core.model.entities.remote.RemoteUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WelcomeActivityViewModel @Inject constructor(
    savedState: SavedStateHandle,
    private val bookInterestRepo: IBookInterestRepo,
    private val userRepo: IUserRepo,
    private val userAuth: IUserAuth,
    private val appUtil: AppUtil,
): ViewModel() {

    private val aCollaboratorOrUserReferralId = savedState.get<String>(Referrer.ID)
    private var remoteUser: MutableLiveData<RemoteUser?> = MutableLiveData()
    private var isNavigatedFromLogin:Boolean = false

    fun isNavigatedFromLogin(): Boolean {
        return isNavigatedFromLogin
    }

    fun setIsNavigatedFromLogin(value:Boolean){
        isNavigatedFromLogin = value
    }

    fun getACollaboratorOrUserReferralId(): String? {
        return aCollaboratorOrUserReferralId
    }

    fun getUserReferralId(): String? {
        if(aCollaboratorOrUserReferralId != null){
            val sampleAttachedEarningsCurrency = Payment.SAMPLE_ATTACHED_EARNINGS_CURRENCY
            val referrerIdAndEarningsCurrencyLength = Auth.USER_ID_LENGTH + sampleAttachedEarningsCurrency.length
            val isUserReferralId =  aCollaboratorOrUserReferralId.length > referrerIdAndEarningsCurrencyLength
            if(isUserReferralId){
                return aCollaboratorOrUserReferralId
            }
        }
        return null
    }

    private fun addBookInterest(bookInterest: BookInterest){
        viewModelScope.launch {
            bookInterestRepo.addBookInterest(bookInterest)
        }
    }

    fun updateUserDeviceType(user: User) {
        val userDevice = DeviceUtil.getDeviceBrandAndModel()
        val userOS = DeviceUtil.getDeviceOSVersionInfo(Build.VERSION.SDK_INT)
        val userSignedInWithADiffDevice = user.device != userDevice || user.deviceOs!= userOS

        if (userSignedInWithADiffDevice){
            user.device =   userDevice
            user.deviceOs= userOS
            user.appVersion=appUtil.getAppVersionName()
        }else {
            user.uploaded = true
        }

        viewModelScope.launch {
            userRepo.addUser(user)
        }
    }

    fun addRemoteBookInterest(bookInterest: BookInterest?){
        bookInterest?.let {
            bookInterest.uploaded=true
            addBookInterest(it)
        }
    }

     fun getRemoteUser(): LiveData<RemoteUser?> {
         viewModelScope.launch {
             try {
                remoteUser.value = userRepo.getRemoteUser(userAuth.getUserId())
             }catch (e:Exception){
                 ErrorUtil.e(e)
                 return@launch
             }
         }
       return remoteUser
    }

}