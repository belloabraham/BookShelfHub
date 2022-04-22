package com.bookshelfhub.bookshelfhub

import androidx.lifecycle.*
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import com.bookshelfhub.bookshelfhub.helpers.utils.settings.SettingsUtil
import com.bookshelfhub.bookshelfhub.data.models.entities.BookInterest
import com.bookshelfhub.bookshelfhub.data.models.entities.Collaborator
import com.bookshelfhub.bookshelfhub.data.repos.bookinterest.IBookInterestRepo
import com.bookshelfhub.bookshelfhub.data.repos.referral.IReferralRepo
import com.bookshelfhub.bookshelfhub.helpers.dynamiclink.Referrer
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.helpers.dynamiclink.IDynamicLink
import com.bookshelfhub.bookshelfhub.helpers.dynamiclink.Social
import com.bookshelfhub.bookshelfhub.helpers.remoteconfig.IRemoteConfig
import com.bookshelfhub.bookshelfhub.helpers.utils.ConnectionUtil
import com.bookshelfhub.bookshelfhub.workers.*
import com.google.common.base.Optional
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    val savedState: SavedStateHandle,
    val userAuth: IUserAuth,
    private val worker: Worker,
    private val remoteConfig: IRemoteConfig,
    private val dynamicLink: IDynamicLink,
    private val settingsUtil: SettingsUtil,
    private val bookInterestRepo: IBookInterestRepo,
    private val connectionUtil: ConnectionUtil,
    private val referralRepo: IReferralRepo
    ):ViewModel() {

    private var bottomBarSelectedIndex: MutableLiveData<Int> = MutableLiveData()
    private var isNewMoreTabNotif: MutableLiveData<Boolean> = MutableLiveData()
    private var verifyPhoneOrEmailNotifNo: MutableLiveData<Int> = MutableLiveData()
    private var newAppUpdateNotifNo: MutableLiveData<Int> = MutableLiveData()
    private var bookInterestNotifNo: MutableLiveData<Int> = MutableLiveData()
    private val userId:String = userAuth.getUserId()
    private var onBackPressed: MutableLiveData<Boolean> = MutableLiveData()


    private val aCollaboratorOrUserReferralId = savedState.get<String>(Referrer.ID)
    private val ACTIVE_VIEW_PAGER="active_view_pager"
    private val ACTIVE_PAGE="active_page"

    init {
        verifyPhoneOrEmailNotifNo.value=0
        newAppUpdateNotifNo.value=0
        bookInterestNotifNo.value=0
        getRemotePrivateKeys()
    }

    fun getBookIdFromACollaboratorReferrerId(): String? {
        val referrerIsACollaborator = aCollaboratorOrUserReferralId != null && aCollaboratorOrUserReferralId.length > userId.length

        if(referrerIsACollaborator){
            val collaboratorAndBookId = aCollaboratorOrUserReferralId!!.split(Referrer.SEPARATOR)
            val collaboratorId = collaboratorAndBookId[0]
            val bookId = collaboratorAndBookId[1]

            val collaborator = Collaborator(collaboratorId, bookId)
            addCollaborator(collaborator)
            return bookId
        }
        return null
    }


     fun getAndSaveAppShareDynamicLink(){
        viewModelScope.launch {
            if(settingsUtil.getString(Referrer.REF_LINK) == null && connectionUtil.isConnected()){
                val title = remoteConfig.getString(Social.TITLE)
                val description = remoteConfig.getString(Social.DESC)
                val imageUrl = remoteConfig.getString(Social.IMAGE_URL)
                try {
                    dynamicLink.generateShortDynamicLinkAsync(title, description, imageUrl, userId)?.let {
                        settingsUtil.setString(Referrer.REF_LINK, it.toString())
                    }
                }catch (e:Exception){
                    Timber.e(e)
                }
            }
        }
    }

    private fun getRemotePrivateKeys(){
        val oneTimeRemotePrivateKeyWorker =
            OneTimeWorkRequestBuilder<GetRemotePrivateKeys>()
                .setConstraints(Constraint.getConnected())
                .build()
        worker.enqueueUniqueWork(
            Tag.oneTimeRemotePrivateKeyWorker,
            ExistingWorkPolicy.REPLACE,
            oneTimeRemotePrivateKeyWorker
        )
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


    private fun addCollaborator(collaborator: Collaborator){
        viewModelScope.launch {
            referralRepo.addPubReferrer(collaborator)
        }
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

    private fun setBookInterestNotifNo(value:Int){
        bookInterestNotifNo.value=value
        isNewMoreTabNotif.value=true
    }


    fun getBookInterest():LiveData<Optional<BookInterest>>{
        return bookInterestRepo.getLiveBookInterest(userId)
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
        //Set that there is a new notification in more tab
        isNewMoreTabNotif.value=true
    }

}