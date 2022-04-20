package com.bookshelfhub.bookshelfhub

import android.os.Build
import androidx.lifecycle.*
import com.bookshelfhub.bookshelfhub.data.models.entities.BookInterest
import com.bookshelfhub.bookshelfhub.data.models.entities.User
import com.bookshelfhub.bookshelfhub.data.models.entities.remote.RemoteUser
import com.bookshelfhub.bookshelfhub.data.repos.BookInterestRepo
import com.bookshelfhub.bookshelfhub.data.repos.UserRepo
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.RemoteDataFields
import com.bookshelfhub.bookshelfhub.helpers.Json
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.helpers.dynamiclink.Referrer
import com.bookshelfhub.bookshelfhub.helpers.utils.AppUtil
import com.bookshelfhub.bookshelfhub.helpers.utils.DeviceUtil
import com.google.firebase.firestore.DocumentSnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WelcomeActivityViewModel @Inject constructor(
    val savedState: SavedStateHandle,
    private val bookInterestRepo: BookInterestRepo,
    private val userRepo: UserRepo,
    userAuth: IUserAuth,
    private val json: Json,
    private val appUtil: AppUtil,
): ViewModel() {

    private val referrer = savedState.get<String>(Referrer.ID)
    private val userId = userAuth.getUserId()
    private var remoteUser: MutableLiveData<RemoteUser?> = MutableLiveData()

    fun getReferrer(): String? {
        return referrer
    }

    private fun addBookInterest(bookInterest:BookInterest){
        viewModelScope.launch {
            bookInterestRepo.addBookInterest(bookInterest)
        }
    }

    fun updateUserDeviceType(user:User) {

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

     fun getRemoteUserDataSnapshot(): LiveData<RemoteUser?> {
         viewModelScope.launch {
             try {
                 val docSnapshot = userRepo.getRemoteUserDataSnapshot(userId)
                 remoteUser.value = docSnapshot
             }catch (e:Exception){
                 return@launch
             }
         }
       return remoteUser
    }


}