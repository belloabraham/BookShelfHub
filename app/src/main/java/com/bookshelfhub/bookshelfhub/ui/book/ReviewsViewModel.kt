package com.bookshelfhub.bookshelfhub.ui.book

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.bookshelfhub.bookshelfhub.book.Book
import com.bookshelfhub.bookshelfhub.services.database.cloud.DbFields
import com.bookshelfhub.bookshelfhub.services.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.services.database.cloud.ICloudDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.UserReview
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ReviewsViewModel @Inject constructor(
    val cloudDb: ICloudDb, val savedState: SavedStateHandle, val userAuth:IUserAuth ): ViewModel(){

    val userId = userAuth.getUserId()
    private var userReviews: MutableLiveData<List<UserReview>> = MutableLiveData()
    val isbn = savedState.get<String>(Book.ISBN.KEY)!!

    init {

        cloudDb.getListOfDataAsync(DbFields.PUBLISHED_BOOKS.KEY, isbn, DbFields.REVIEWS.KEY, UserReview::class.java, DbFields.VERIFIED.KEY, whereValue = true, userId, limit = 300){ reviews, _->
            userReviews.value = reviews
        }
    }

    fun getUserReviews(): LiveData<List<UserReview>> {
        return userReviews
    }

}