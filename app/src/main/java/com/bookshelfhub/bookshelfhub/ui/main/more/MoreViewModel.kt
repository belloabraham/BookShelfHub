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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MoreViewModel @Inject constructor(
    private val userReviewRepo: IUserReviewRepo,
    private val orderedBooksRepo: IOrderedBooksRepo,
    private val bookmarksRepo: IBookmarksRepo,
    private val userRepo: IUserRepo,
    val userAuth: IUserAuth,
    private val earningsRepo: EarningsRepo,
    private val paymentCardRepo: IPaymentCardRepo
): ViewModel(){

    private val userId:String = userAuth.getUserId()

     suspend fun getLiveTotalEarnings(): Earnings? {
       return earningsRepo.getRemoteEarnings(userId)
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