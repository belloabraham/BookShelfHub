package com.bookshelfhub.bookshelfhub

import androidx.lifecycle.*
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import com.bookshelfhub.bookshelfhub.data.models.ApiKeys
import com.bookshelfhub.bookshelfhub.helpers.utils.settings.SettingsUtil
import com.bookshelfhub.bookshelfhub.data.models.entities.BookInterest
import com.bookshelfhub.bookshelfhub.data.models.entities.PubReferrers
import com.bookshelfhub.bookshelfhub.data.models.entities.StoreSearchHistory
import com.bookshelfhub.bookshelfhub.data.models.entities.User
import com.bookshelfhub.bookshelfhub.data.repos.*
import com.bookshelfhub.bookshelfhub.helpers.dynamiclink.Referrer
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.IRemoteDataSource
import com.bookshelfhub.bookshelfhub.helpers.utils.settings.Settings
import com.bookshelfhub.bookshelfhub.workers.RecommendedBooks
import com.bookshelfhub.bookshelfhub.workers.Tag
import com.bookshelfhub.bookshelfhub.workers.Worker
import com.google.common.base.Optional
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    val remoteDataSource: IRemoteDataSource,
    val savedState: SavedStateHandle,
    val settingsUtil: SettingsUtil,
    val userAuth: IUserAuth,
    userRepo:UserRepo,
    private val worker: Worker,
    private val privateKeysRepo: PrivateKeysRepo,
    bookInterestRepo: BookInterestRepo,
    searchHistoryRepo: SearchHistoryRepo,
    private val referralRepo: ReferralRepo
    ):ViewModel() {

    private var bottomBarSelectedIndex: MutableLiveData<Int> = MutableLiveData()
    private var isNewMoreTabNotif: MutableLiveData<Boolean> = MutableLiveData()
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


    private val referrer = savedState.get<String>(Referrer.ID)
    private val ACTIVE_VIEW_PAGER="active_view_pager"
    private val ACTIVE_PAGE="active_page"

    init {
        verifyPhoneOrEmailNotifNo.value=0
        newAppUpdateNotifNo.value=0
        bookInterestNotifNo.value=0
        user=userRepo.getLiveUser(userId)
        bookInterest = bookInterestRepo.getLiveBookInterest(userId)
        storeSearchHistory = searchHistoryRepo.getLiveStoreSearchHistory(userId)

        getRemotePrivateKeys()

    }

    fun updatedRecommendedBooks(bookInterest: Optional<BookInterest>){
        if (bookInterest.isPresent && bookInterest.get().added) {
            setBookInterestNotifNo(0)
            val recommendedBooksWorker =
                OneTimeWorkRequestBuilder<RecommendedBooks>()
                    .build()
            worker.enqueueUniqueWork(
                Tag.recommendedBooksWorker,
                ExistingWorkPolicy.REPLACE,
                recommendedBooksWorker
            )
        }else {
            setBookInterestNotifNo(1)
        }
    }

    private fun getRemotePrivateKeys(){
        viewModelScope.launch {
            try {
                privateKeysRepo.getPrivateKeys(Settings.API_KEYS, ApiKeys::class.java)?.let {
                    settingsUtil.setString(
                        Settings.PERSPECTIVE_API,
                        it.perspectiveKey!!
                    )
                    settingsUtil.setString(
                        Settings.FIXER_ENDPOINT,
                        it.fixerEndpoint!!
                    )
                    settingsUtil.setString(
                        Settings.FLUTTER_ENCRYPTION,
                        it.flutterEncKey!!
                    )
                    settingsUtil.setString(
                        Settings.FLUTTER_PUBLIC,
                        it.flutterPublicKey!!
                    )
                }
            }catch (e:Exception){ }
        }
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

    fun addPubReferrer(pubReferrer: PubReferrers){
        viewModelScope.launch {
            referralRepo.addPubReferrer(pubReferrer)
        }
    }

    fun getStoreSearchHistory():LiveData<List<StoreSearchHistory>>{
        return storeSearchHistory
    }

    fun getTotalMoreTabNotification(): Int {
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
        isNewMoreTabNotif.value=true
    }

     fun setVerifyPhoneOrEmailNotif(value:Int){
        verifyPhoneOrEmailNotifNo.value = value
        isNewMoreTabNotif.value=true
    }

    fun getBookInterest():LiveData<Optional<BookInterest>>{
        return bookInterest
    }

    fun getUserRecord():LiveData<User>{
        return user
    }

    fun getIsNewMoreTabNotif():LiveData<Boolean>{
        return isNewMoreTabNotif
    }

    fun setSelectedIndex(index:Int){
        bottomBarSelectedIndex.value=index
    }

    fun getSelectedIndex():LiveData<Int>{
        return bottomBarSelectedIndex
    }

    fun setIsNewUpdate(){
        newAppUpdateNotifNo.value = 1
        //TODO set that there is a new notification in more tab
        isNewMoreTabNotif.value=true
    }

}