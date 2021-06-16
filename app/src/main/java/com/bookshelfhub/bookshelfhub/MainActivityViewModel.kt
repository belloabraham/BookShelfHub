package com.bookshelfhub.bookshelfhub

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bookshelfhub.bookshelfhub.Utils.AppUtil
import com.bookshelfhub.bookshelfhub.Utils.SettingsUtil
import com.bookshelfhub.bookshelfhub.config.RemoteConfig
import com.bookshelfhub.bookshelfhub.services.database.Database
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(private val remoteConfig:RemoteConfig, private val appUtil: AppUtil, private val settingsUtil: SettingsUtil,database: Database):ViewModel() {
    private var isUpdateAvailable: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    private var bottomBarSelectedIndex: MutableLiveData<Int> = MutableLiveData<Int>()
    //private var user: LiveData<List<User>>
    private val NEW_VERSION_CODE="new_version_code"
    private val APP_VERSION_CODE="app_version_code"


    init {
        checkForUpdate()
        //user=database.getUsers()
    }

  //  fun getUserData():LiveData<List<User>>{
        //return user
   // }

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
                }
            }

        }
    }

}