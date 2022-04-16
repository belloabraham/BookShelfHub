package com.bookshelfhub.bookshelfhub.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bookshelfhub.bookshelfhub.data.repos.*
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
    private val paymentCardRepo: PaymentCardRepo): ViewModel(){

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