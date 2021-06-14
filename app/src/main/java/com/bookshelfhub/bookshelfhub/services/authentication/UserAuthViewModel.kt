package com.bookshelfhub.bookshelfhub.services.authentication

import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bookshelfhub.bookshelfhub.Utils.AppUtil
import com.bookshelfhub.bookshelfhub.Utils.DateTimeUtil
import com.bookshelfhub.bookshelfhub.Utils.DeviceUtil
import com.bookshelfhub.bookshelfhub.services.database.Database
import com.bookshelfhub.bookshelfhub.models.IUser
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.UserRecord
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserAuthViewModel @Inject constructor(private val database:Database, private val deviceUtil:DeviceUtil, private val appUtil: AppUtil, private val userAuth: UserAuth): ViewModel(){
    private var isAddingUser: MutableLiveData<Boolean>  = MutableLiveData<Boolean>()
    private var isExistingUser: MutableLiveData<Boolean> = MutableLiveData<Boolean>()


    fun setIsExistingUser(value:Boolean){
        isExistingUser.value=value
    }

    fun getIsExistingUser(): LiveData<Boolean> {
        return isExistingUser
    }

    fun setIsAddingUser(value:Boolean, userData: IUser){
        viewModelScope.launch {
            val localDateTime= DateTimeUtil.getDateTimeAsString()
            val user = UserRecord(userAuth.getUserId(),userData.name, userData.email,userData.phone, userData.photoUri, userAuth.getAuthType(),appUtil.getAppVersionName(), deviceUtil.getDeviceBrandAndModel(), deviceUtil.getDeviceOSVersionInfo(
                Build.VERSION.SDK_INT), localDateTime )
            database.addUser(user)
        }
        isAddingUser.value=value
    }

    fun getIsAddingUser(): LiveData<Boolean> {
        return isAddingUser
    }

}