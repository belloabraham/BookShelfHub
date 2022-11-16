package com.bookshelfhub.feature.home.ui.more

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bookshelfhub.core.authentication.IUserAuth
import com.bookshelfhub.core.data.repos.bookmarks.IBookmarksRepo
import com.bookshelfhub.core.data.repos.earnings.EarningsRepo
import com.bookshelfhub.core.data.repos.ordered_books.IOrderedBooksRepo
import com.bookshelfhub.core.data.repos.payment_card.IPaymentCardRepo
import com.bookshelfhub.core.data.repos.user.IUserRepo
import com.bookshelfhub.core.data.repos.user_review.IUserReviewRepo
import com.bookshelfhub.core.datastore.settings.SettingsUtil
import com.bookshelfhub.core.model.Earnings
import com.bookshelfhub.core.model.entities.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
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

     suspend fun getRemoteEarnings(): Earnings? {
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

    suspend fun getUserRecord(): Optional<User> {
        return userRepo.getUser(userId)
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