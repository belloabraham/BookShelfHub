package com.bookshelfhub.bookshelfhub

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bookshelfhub.bookshelfhub.config.RemoteConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(private val remoteConfig:RemoteConfig):ViewModel() {
    private var isUpdateAvailable: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    private val NEW_VERSION_CODE = "new_version_code"

    init {

    }

    private fun checkforUpdate(){
        remoteConfig.fetchConfigAsync({
         val newVersonCode =  remoteConfig.getLong(NEW_VERSION_CODE)
        },{})
    }

}