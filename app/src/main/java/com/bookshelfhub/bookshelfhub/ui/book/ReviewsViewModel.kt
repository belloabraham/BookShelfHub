package com.bookshelfhub.bookshelfhub.ui.book

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.bookshelfhub.bookshelfhub.data.enums.Book
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.RemoteDataFields
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.IRemoteDataSource
import com.bookshelfhub.bookshelfhub.data.models.entities.UserReview
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ReviewsViewModel @Inject constructor(
    val remoteDataSource: IRemoteDataSource,
    val savedState: SavedStateHandle,
    val userAuth:IUserAuth ): ViewModel(){

    val userId = userAuth.getUserId()
    private var userReviews: MutableLiveData<List<UserReview>> = MutableLiveData()
    val isbn = savedState.get<String>(Book.ISBN.KEY)!!

    init {
        remoteDataSource.getListOfDataWhereAsync(RemoteDataFields.PUBLISHED_BOOKS.KEY, isbn, RemoteDataFields.REVIEWS_COLL.KEY, UserReview::class.java, RemoteDataFields.VERIFIED.KEY, whereValue = true, userId, limit = 300){ reviews, _->
            userReviews.value = reviews
        }
    }

    fun getUserReviews(): LiveData<List<UserReview>> {
        return userReviews
    }

}