package com.bookshelfhub.feature.home

import androidx.lifecycle.*
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import com.bookshelfhub.core.authentication.IUserAuth
import com.bookshelfhub.core.common.helpers.ErrorUtil
import com.bookshelfhub.core.common.helpers.utils.ConnectionUtil
import com.bookshelfhub.core.common.worker.Constraint
import com.bookshelfhub.core.common.worker.Tag
import com.bookshelfhub.core.common.worker.Worker
import com.bookshelfhub.core.data.repos.bookinterest.IBookInterestRepo
import com.bookshelfhub.core.data.repos.referral.IReferralRepo
import com.bookshelfhub.core.datastore.settings.Settings
import com.bookshelfhub.core.datastore.settings.SettingsUtil
import com.bookshelfhub.core.dynamic_link.IDynamicLink
import com.bookshelfhub.core.dynamic_link.Referrer
import com.bookshelfhub.core.dynamic_link.Social
import com.bookshelfhub.core.model.entities.BookInterest
import com.bookshelfhub.core.model.entities.Collaborator
import com.bookshelfhub.core.remote.remote_config.IRemoteConfig
import com.bookshelfhub.feature.home.workers.GetRemotePrivateKeys
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    savedState: SavedStateHandle,
    val userAuth: IUserAuth,
    private val worker: Worker,
    private val remoteConfig: IRemoteConfig,
    private val dynamicLink: IDynamicLink,
    private val settingsUtil: SettingsUtil,
    private val bookInterestRepo: IBookInterestRepo,
    private val connectionUtil: ConnectionUtil,
    private val referralRepo: IReferralRepo,
    ):ViewModel() {

    private var bottomBarSelectedIndex: MutableLiveData<Int> = MutableLiveData()
    private var isNewMoreTabNotif: MutableLiveData<Boolean> = MutableLiveData()
    private var verifyPhoneOrEmailNotifNo: MutableLiveData<Int> = MutableLiveData()
    private var newAppUpdateNotifNo: MutableLiveData<Int> = MutableLiveData()
    private var bookInterestNotifNo: MutableLiveData<Int> = MutableLiveData()
    private val userId:String = userAuth.getUserId()
    private var onBackPressed: MutableLiveData<Boolean> = MutableLiveData()


    private val aCollaboratorOrUserReferralId = savedState.get<String>(Referrer.ID)

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
            val collabCommission =   collaboratorAndBookId[2]

            val collaborator = Collaborator(collaboratorId, bookId, collabCommission.toDouble())
            addCollaborator(collaborator)
            return bookId
        }
        return null
    }


    suspend fun isDarkUserPreferedTheme(): Boolean {
        return settingsUtil.getBoolean(Settings.IS_DARK_THEME, false)
    }

    suspend fun setIsDarkTheme(value:Boolean){
        settingsUtil.setBoolean(Settings.IS_DARK_THEME, value)
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
                    ErrorUtil.e(e)
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


    fun updatedRecommendedBooksNotification(bookInterest: Optional<BookInterest>){
         if (bookInterest.isPresent && bookInterest.get().added) {
            setBookInterestNotifNo(0)
        }else {
            setBookInterestNotifNo(1)
        }
    }

    private fun addCollaborator(collaborator: Collaborator){
        viewModelScope.launch {
            referralRepo.addCollaboratorOrIgnore(collaborator)
        }
    }


    fun getTotalMoreTabNotification(): Int {
        return newAppUpdateNotifNo.value!! + verifyPhoneOrEmailNotifNo.value!! + bookInterestNotifNo.value!!
    }

    fun getIsNewAppUpdateNotificationNumber():LiveData<Int>{
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

    fun getLiveOptionalBookInterest():LiveData<Optional<BookInterest>>{
        return bookInterestRepo.getLiveBookInterest(userId)
    }

    fun getIsNewNotificationAtMoreTab():LiveData<Boolean>{
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