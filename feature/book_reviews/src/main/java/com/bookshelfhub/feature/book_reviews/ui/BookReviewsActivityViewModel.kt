package com.bookshelfhub.feature.book_reviews.ui

import androidx.lifecycle.*
import com.bookshelfhub.core.authentication.IUserAuth
import com.bookshelfhub.core.common.helpers.ErrorUtil
import com.bookshelfhub.core.data.Book
import com.bookshelfhub.core.data.repos.published_books.IPublishedBooksRepo
import com.bookshelfhub.core.data.repos.user_review.IUserReviewRepo
import com.bookshelfhub.core.model.entities.PublishedBook
import com.bookshelfhub.core.model.entities.UserReview
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class BookReviewsActivityViewModel @Inject constructor(
    savedState: SavedStateHandle,
    publishedBooksRepo: IPublishedBooksRepo,
    private val userReviewRepo: IUserReviewRepo,
    userAuth: IUserAuth
): ViewModel() {

    private val bookId =  savedState.get<String>(Book.ID)!!

    private val userId = userAuth.getUserId()

    private var userReviews: MutableLiveData<List<UserReview>> = MutableLiveData()
    private var localLivePublishedBook: LiveData<Optional<PublishedBook>> = MutableLiveData()

    init {
        localLivePublishedBook = publishedBooksRepo.getALiveOptionalPublishedBook(bookId)
        get300RemoteBookReviews()
    }

    private fun get300RemoteBookReviews(){
        viewModelScope.launch {
            try {
                userReviews.value = userReviewRepo.getRemoteListOfBookReviews(bookId, limit = 300, userId)
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