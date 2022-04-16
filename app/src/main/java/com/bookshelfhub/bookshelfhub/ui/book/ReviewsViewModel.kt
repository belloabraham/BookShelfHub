package com.bookshelfhub.bookshelfhub.ui.book

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.bookshelfhub.bookshelfhub.data.Book
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.data.models.entities.UserReview
import com.bookshelfhub.bookshelfhub.data.repos.UserReviewRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ReviewsViewModel @Inject constructor(
    val savedState: SavedStateHandle,
    userReviewRepo: UserReviewRepo,
    val userAuth:IUserAuth ): ViewModel(){

    val userId = userAuth.getUserId()
    private var userReviews: MutableLiveData<List<UserReview>> = MutableLiveData()
    val bookId = savedState.get<String>(Book.ISBN)!!

    init {
        userReviewRepo.getRemoteBookReviews(bookId, userId, limitBy = 300){  reviews, _->
            userReviews.postValue(reviews)
        }
    }

    fun getTop300UserReviews(): LiveData<List<UserReview>> {
        return userReviews
    }

}