package com.bookshelfhub.bookshelfhub

import androidx.lifecycle.*
import com.bookshelfhub.bookshelfhub.data.Book
import com.bookshelfhub.bookshelfhub.data.Fragment
import com.bookshelfhub.bookshelfhub.data.models.entities.PublishedBook
import com.bookshelfhub.bookshelfhub.data.models.entities.UserReview
import com.bookshelfhub.bookshelfhub.data.repos.publishedbooks.IPublishedBooksRepo
import com.bookshelfhub.bookshelfhub.data.repos.userreview.IUserReviewRepo
import com.bookshelfhub.bookshelfhub.domain.usecases.GetBookIdFromCompoundId
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import com.google.common.base.Optional
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class BookInfoActivityViewModel @Inject constructor(
    val savedState: SavedStateHandle,
    publishedBooksRepo: IPublishedBooksRepo,
    private val userReviewRepo: IUserReviewRepo,
    val userAuth: IUserAuth,
    private val getBookIdFromCompoundId: GetBookIdFromCompoundId,
): ViewModel() {
    private val title =  savedState.get<String>(Book.NAME)!!
    private val fragmentId = savedState.get<Int>(Fragment.ID)!!
    private val bookId =   savedState.get<String>(Book.ID)!!

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
                Timber.e(e)
                return@launch
            }
        }
    }

    fun getLivePublishedBook(): LiveData<Optional<PublishedBook>> {
        return localLivePublishedBook
    }


    fun getFragmentId(): Int {
        return fragmentId
    }
    fun getTitle(): String {
        return title
    }

    fun getBookIdFromCompoundId(): String {
        return getBookIdFromCompoundId.invoke(bookId)
    }

    fun getTop300UserReviews(): LiveData<List<UserReview>> {
        return userReviews
    }

}