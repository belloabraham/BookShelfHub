package com.bookshelfhub.bookshelfhub

import androidx.lifecycle.*
import com.bookshelfhub.bookshelfhub.helpers.dynamiclink.Referrer
import com.bookshelfhub.bookshelfhub.services.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.services.database.cloud.ICloudDb
import com.bookshelfhub.bookshelfhub.services.database.local.ILocalDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.*
import com.google.common.base.Optional
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    val cloudDb: ICloudDb,
    val localDb: ILocalDb,
    val savedState: SavedStateHandle,
    val userAuth: IUserAuth
    ):ViewModel() {

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

    private val referrer = savedState.get<String>(Referrer.ID.KEY)
    private val ACTIVE_VIEW_PAGER="active_view_pager"
    private val ACTIVE_PAGE="active_page"

    init {
        verifyPhoneOrEmailNotifNo.value=0
        newAppUpdateNotifNo.value=0
        bookInterestNotifNo.value=0
        user=localDb.getLiveUser(userId)
        bookInterest = localDb.getLiveBookInterest(userId)
        storeSearchHistory = localDb.getLiveStoreSearchHistory(userId)

    }

    fun setActivePage(value:Int){
        savedState.set(ACTIVE_PAGE,value)
    }

    fun getActivePage(): Int? {
        return  savedState.get<Int>(ACTIVE_PAGE)
    }

    fun getActiveViewPager(): Int? {
      return savedState.get<Int>(ACTIVE_VIEW_PAGER)
    }

    fun setActiveViewPager(value:Int){
        savedState.set(ACTIVE_VIEW_PAGER,value)
    }

    fun getReferrer(): String? {
        return referrer
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