package com.bookshelfhub.bookshelfhub.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import com.bookshelfhub.bookshelfhub.data.models.entities.BookInterest
import com.bookshelfhub.bookshelfhub.data.models.entities.User
import com.bookshelfhub.bookshelfhub.data.repos.*
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.helpers.dynamiclink.IDynamicLink
import com.bookshelfhub.bookshelfhub.helpers.dynamiclink.Referrer
import com.bookshelfhub.bookshelfhub.helpers.dynamiclink.Social
import com.bookshelfhub.bookshelfhub.helpers.remoteconfig.IRemoteConfig
import com.bookshelfhub.bookshelfhub.helpers.utils.settings.SettingsUtil
import com.bookshelfhub.bookshelfhub.workers.RecommendedBooks
import com.bookshelfhub.bookshelfhub.workers.Tag
import com.google.common.base.Optional
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoreViewModel @Inject constructor(
    private val userReviewRepo: UserReviewRepo,
    private val orderedBooksRepo: OrderedBooksRepo,
    private val bookmarksRepo: BookmarksRepo,
    private val userRepo: UserRepo,
    val userAuth: IUserAuth,
    private val paymentCardRepo: PaymentCardRepo): ViewModel(){

    private val userId:String = userAuth.getUserId()


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