package com.bookshelfhub.feature.book_reviews

import androidx.lifecycle.*
import com.bookshelfhub.core.authentication.IUserAuth
import com.bookshelfhub.core.common.helpers.ErrorUtil
import com.bookshelfhub.core.data.Book
import com.bookshelfhub.core.data.repos.user_review.IUserReviewRepo
import com.bookshelfhub.core.model.entities.UserReview
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookReviewsActivityViewModel @Inject constructor(
    savedState: SavedStateHandle,
    private val userReviewRepo: IUserReviewRepo,
    userAuth: IUserAuth
): ViewModel() {

    private val bookId =  savedState.get<String>(Book.ID)!!

    private val userId = userAuth.getUserId()

    private var userReviews: MutableLiveData<List<UserReview>> = MutableLiveData()

    init {
        get300RemoteBookReviews()
    }

    private fun get300RemoteBookReviews(){
        viewModelScope.launch {
            try {
                val reviews = userReviewRepo.getRemoteListOfBookReviews(bookId, limit = 300, userId)
                userReviews.value = reviews
            }catch (e:Exception){
                ErrorUtil.e(e)
                return@launch
            }
        }
    }

    fun getTop300UserReviews(): LiveData<List<UserReview>> {
        return userReviews
    }

}