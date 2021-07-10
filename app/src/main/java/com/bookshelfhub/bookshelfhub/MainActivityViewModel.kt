package com.bookshelfhub.bookshelfhub

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bookshelfhub.bookshelfhub.Utils.AppUtil
import com.bookshelfhub.bookshelfhub.Utils.SettingsUtil
import com.bookshelfhub.bookshelfhub.config.RemoteConfig
import com.bookshelfhub.bookshelfhub.services.authentication.UserAuth
import com.bookshelfhub.bookshelfhub.services.database.local.LocalDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.*
import com.google.common.base.Optional
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(private val remoteConfig:RemoteConfig, private val appUtil: AppUtil, private val settingsUtil: SettingsUtil, val localDb: LocalDb, val userAuth: UserAuth):ViewModel() {
    private var isUpdateAvailable: MutableLiveData<Boolean> = MutableLiveData()
    private var bottomBarSelectedIndex: MutableLiveData<Int> = MutableLiveData()
    private var isNewProfileNotif: MutableLiveData<Boolean> = MutableLiveData()
    private var user: LiveData<User> = MutableLiveData()
    private var bookInterest: LiveData<Optional<BookInterest>> = MutableLiveData()
    private val NEW_VERSION_CODE="new_version_code"
    private val APP_VERSION_CODE="app_version_code"
    private var verifyPhoneOrEmailNotifNo: MutableLiveData<Int> = MutableLiveData()
    private var newAppUpdateNotifNo: MutableLiveData<Int> = MutableLiveData()
    private var bookInterestNotifNo: MutableLiveData<Int> = MutableLiveData()
    private var shelfShelfSearchHistory: LiveData<List<ShelfSearchHistory>> = MutableLiveData()
    private var storeSearchHistory: LiveData<List<StoreSearchHistory>> = MutableLiveData()
    private var orderedBooks: LiveData<List<OrderedBooks>> = MutableLiveData()
    private val userId:String = userAuth.getUserId()
    private var userReferralLink:String?=null
    private var onBackPressed: MutableLiveData<Boolean> = MutableLiveData()


    init {
        verifyPhoneOrEmailNotifNo.value=0
        newAppUpdateNotifNo.value=0
        bookInterestNotifNo.value=0
        checkForUpdate()
        user=localDb.getLiveUser(userId)
        bookInterest = localDb.getLiveBookInterest(userId)
        shelfShelfSearchHistory = localDb.getLiveShelfSearchHistory(userId)
        storeSearchHistory = localDb.getLiveStoreSearchHistory(userId)
        orderedBooks = localDb.getLiveOrderedBooks(userId)
    }


    fun getOrderedBooks():LiveData<List<OrderedBooks>>{
        return orderedBooks
    }

    fun setUserReferralLink(value:String){
        userReferralLink=value
    }

    fun getUserReferralLink():String?{
        return userReferralLink
    }

    fun getUserId():String{
        return userId
    }

    fun addPubReferrer(pubReferrer:PubReferrers){
       viewModelScope.launch(IO){
           localDb.addPubReferrer(pubReferrer)
       }
    }

     fun getShelfSearchHistory():LiveData<List<ShelfSearchHistory>>{
         return shelfShelfSearchHistory
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


    fun setOnBackPressed( value:Boolean){
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
                        newAppUpdateNotifNo.value = 1
                        isNewProfileNotif.value=true
                    }else{
                        newAppUpdateNotifNo.value = 0
                        isNewProfileNotif.value=false
                    }
                }
            }

        }
    }

}