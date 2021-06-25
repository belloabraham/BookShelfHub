package com.bookshelfhub.bookshelfhub

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bookshelfhub.bookshelfhub.Utils.AppUtil
import com.bookshelfhub.bookshelfhub.Utils.SettingsUtil
import com.bookshelfhub.bookshelfhub.config.RemoteConfig
import com.bookshelfhub.bookshelfhub.services.authentication.UserAuth
import com.bookshelfhub.bookshelfhub.services.database.Database
import com.bookshelfhub.bookshelfhub.services.database.local.LocalDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.UserRecord
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(private val remoteConfig:RemoteConfig, private val appUtil: AppUtil, private val settingsUtil: SettingsUtil, private val localDb: LocalDb, private val userAuth: UserAuth):ViewModel() {
    private var isUpdateAvailable: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    private var bottomBarSelectedIndex: MutableLiveData<Int> = MutableLiveData<Int>()
    private var isNewProfileNotif: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    private var user: LiveData<UserRecord> = MutableLiveData()
    private val NEW_VERSION_CODE="new_version_code"
    private val APP_VERSION_CODE="app_version_code"
    private var verifyPhoneOrEmailNotifNumber: MutableLiveData<Int> = MutableLiveData<Int>()
    private var newAppUpdateNotifNumber: MutableLiveData<Int> = MutableLiveData<Int>()


    init {
        verifyPhoneOrEmailNotifNumber.value=0
        newAppUpdateNotifNumber.value=0
        checkForUpdate()
        user=localDb.getLiveUser(userAuth.getUserId())
    }


     fun getTotalProfileNotifNumber():Int{
        return  newAppUpdateNotifNumber.value!!+verifyPhoneOrEmailNotifNumber.value!!
    }

    fun getNewAppUpdateNotifNumber():LiveData<Int>{
      return  newAppUpdateNotifNumber
    }

     fun setVerifyPhoneOrEmailNotif(value:Int){
        verifyPhoneOrEmailNotifNumber.value = value
        isNewProfileNotif.value=true
    }

    fun getUserRecord():LiveData<UserRecord>{
        return user
    }

    fun getIsNewProfileNotif():LiveData<Boolean>{
        return isNewProfileNotif
    }

    fun setSelectedIndex(index:Int){
        bottomBarSelectedIndex.value=index
    }

    fun getSelectedIndex():LiveData<Int>{
        return bottomBarSelectedIndex
    }

    fun getIsUpdateAvailable(): LiveData<Boolean> {
        return isUpdateAvailable
    }

    private fun checkForUpdate(){
        remoteConfig.fetchConfigAsync(){
            //it=error msg
            viewModelScope.launch {
                val newVersionCode =  remoteConfig.getLong(NEW_VERSION_CODE)
                val appVersionCode = settingsUtil.getLong(APP_VERSION_CODE, appUtil.getAppVersionCode())
                withContext(Main){
                    if  (newVersionCode > appVersionCode){
                        isUpdateAvailable.value=true
                        settingsUtil.setLong(APP_VERSION_CODE, newVersionCode)
                    }
                    if(newVersionCode>appUtil.getAppVersionCode()){
                        newAppUpdateNotifNumber.value = 1
                        isNewProfileNotif.value=true
                    }else{
                        newAppUpdateNotifNumber.value = 0
                        isNewProfileNotif.value=false
                    }
                }
            }

        }
    }

}