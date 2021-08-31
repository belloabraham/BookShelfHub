package com.bookshelfhub.bookshelfhub

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bookshelfhub.bookshelfhub.Utils.AppUtil
import com.bookshelfhub.bookshelfhub.Utils.settings.SettingsUtil
import com.bookshelfhub.bookshelfhub.services.remoteconfig.IRemoteConfig
import com.bookshelfhub.bookshelfhub.services.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.services.database.cloud.ICloudDb
import com.bookshelfhub.bookshelfhub.services.database.local.ILocalDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.*
import com.google.common.base.Optional
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(private val remoteConfig:IRemoteConfig, val cloudDb: ICloudDb, private val appUtil: AppUtil, private val settingsUtil: SettingsUtil, val localDb: ILocalDb, val userAuth: IUserAuth):ViewModel() {
    private var bottomBarSelectedIndex: MutableLiveData<Int> = MutableLiveData()
    private var isNewProfileNotif: MutableLiveData<Boolean> = MutableLiveData()
    private var user: LiveData<User> = MutableLiveData()
    private var bookInterest: LiveData<Optional<BookInterest>> = MutableLiveData()
    private var verifyPhoneOrEmailNotifNo: MutableLiveData<Int> = MutableLiveData()
    private var newAppUpdateNotifNo: MutableLiveData<Int> = MutableLiveData()
    private var bookInterestNotifNo: MutableLiveData<Int> = MutableLiveData()
    private var storeSearchHistory: LiveData<List<StoreSearchHistory>> = MutableLiveData()
    private val userId:String = userAuth.getUserId()
    private var userReferralLink:String?=null
    private var onBackPressed: MutableLiveData<Boolean> = MutableLiveData()
    private var isNightMode:MutableLiveData<Boolean>  = MutableLiveData()


    init {
        verifyPhoneOrEmailNotifNo.value=0
        newAppUpdateNotifNo.value=0
        bookInterestNotifNo.value=0
        user=localDb.getLiveUser(userId)
        bookInterest = localDb.getLiveBookInterest(userId)
        storeSearchHistory = localDb.getLiveStoreSearchHistory(userId)

    }

    fun getIsNightMode():LiveData<Boolean>{
       return isNightMode
    }

    fun setIsNightMode(value:Boolean){
        isNightMode.value = value
    }

    fun setUserReferralLink(value:String){
        userReferralLink=value
    }

    fun getUserReferralLink():String?{
        return userReferralLink
    }


    fun addPubReferrer(pubReferrer:PubReferrers){
       viewModelScope.launch(IO){
           localDb.addPubReferrer(pubReferrer)
       }
    }

    fun getStoreSearchHistory():LiveData<List<StoreSearchHistory>>{
        return storeSearchHistory
    }


    fun getTotalProfileNotifNumber(): Int {
        return newAppUpdateNotifNo.value!! + verifyPhoneOrEmailNotifNo.value!! + bookInterestNotifNo.value!!
    }

    fun getNewAppUpdateNotifNumber():LiveData<Int>{
      return  newAppUpdateNotifNo
    }


    fun setOnBackPressed(value:Boolean){
        onBackPressed.value = value
    }
    fun getOnBackPressed():LiveData<Boolean>{
        return onBackPressed
    }

    fun setBookInterestNotifNo(value:Int){
        bookInterestNotifNo.value=value
        isNewProfileNotif.value=true
    }

     fun setVerifyPhoneOrEmailNotif(value:Int){
        verifyPhoneOrEmailNotifNo.value = value
        isNewProfileNotif.value=true
    }

    fun getBookInterest():LiveData<Optional<BookInterest>>{
        return bookInterest
    }

    fun getUserRecord():LiveData<User>{
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

    fun setIsNewUpdate(){
        newAppUpdateNotifNo.value = 1
        isNewProfileNotif.value=true
    }

}