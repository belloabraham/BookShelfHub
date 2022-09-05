package com.bookshelfhub.bookshelfhub.ui.main.more

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bookshelfhub.bookshelfhub.data.models.Earnings
import com.bookshelfhub.bookshelfhub.data.models.entities.User
import com.bookshelfhub.bookshelfhub.data.repos.bookmarks.IBookmarksRepo
import com.bookshelfhub.bookshelfhub.data.repos.earnings.EarningsRepo
import com.bookshelfhub.bookshelfhub.data.repos.orderedbooks.IOrderedBooksRepo
import com.bookshelfhub.bookshelfhub.data.repos.paymentcard.IPaymentCardRepo
import com.bookshelfhub.bookshelfhub.data.repos.user.IUserRepo
import com.bookshelfhub.bookshelfhub.data.repos.userreview.IUserReviewRepo
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.helpers.settings.SettingsUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoreViewModel @Inject constructor(
    private val userReviewRepo: IUserReviewRepo,
    private val orderedBooksRepo: IOrderedBooksRepo,
    private val bookmarksRepo: IBookmarksRepo,
    private val userRepo: IUserRepo,
    private val settingsUtil: SettingsUtil,
    val userAuth: IUserAuth,
    private val earningsRepo: EarningsRepo,
    private val paymentCardRepo: IPaymentCardRepo
): ViewModel(){

    private val userId:String = userAuth.getUserId()

     suspend fun getLiveTotalEarnings(): Earnings? {
       return earningsRepo.getRemoteEarnings(userId)
    }

    suspend fun getString(key: String): String? {
        return settingsUtil.getString(key)
    }

    suspend fun setBoolean(key:String, value:Boolean){
        settingsUtil.setBoolean(key, value)
    }

    suspend fun getBoolean(key:String): Boolean {
       return settingsUtil.getBoolean(key, true)
    }

    fun getLiveUserRecord(): LiveData<User> {
        return userRepo.getLiveUser(userId)
    }

    fun deleteUserData(){
        viewModelScope.launch {
            userRepo.deleteUserRecord()
            userReviewRepo.deleteAllReviews()
            orderedBooksRepo.deleteAllOrderedBooks()
            bookmarksRepo.deleteAllBookmarks()
        }
    }

    fun deleteAllPaymentCards(){
        viewModelScope.launch {
            paymentCardRepo.deleteAllPaymentCards()
        }
    }

}