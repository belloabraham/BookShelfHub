package com.bookshelfhub.bookshelfhub.ui.book

import androidx.lifecycle.*
import com.bookshelfhub.bookshelfhub.data.Book
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.data.models.entities.UserReview
import com.bookshelfhub.bookshelfhub.data.repos.userreview.IUserReviewRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ReviewsViewModel @Inject constructor(
    val savedState: SavedStateHandle,
    userReviewRepo: IUserReviewRepo,
    val userAuth:IUserAuth ): ViewModel(){

    val userId = userAuth.getUserId()
    private var userReviews: MutableLiveData<List<UserReview>> = MutableLiveData()
    val bookId = savedState.get<String>(Book.ID)!!

    init {
        viewModelScope.launch {
            try {
                 userReviews.value = userReviewRepo.getRemoteListOfBookReviews(bookId, limit = 300, userId)
            }catch (e:Exception){
                Timber.e(e)
                return@launch
            }
        }
    }

    fun getTop300UserReviews(): LiveData<List<UserReview>> {
        return userReviews
    }

}