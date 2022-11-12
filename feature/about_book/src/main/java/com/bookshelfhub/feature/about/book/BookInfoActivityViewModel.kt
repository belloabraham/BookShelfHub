package com.bookshelfhub.feature.about.book

import androidx.lifecycle.*
import com.bookshelfhub.core.data.Book
import com.bookshelfhub.core.data.repos.published_books.IPublishedBooksRepo
import com.bookshelfhub.core.domain.usecases.GetBookIdFromCompoundId
import com.bookshelfhub.core.model.entities.PublishedBook
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject

@HiltViewModel
class BookInfoActivityViewModel @Inject constructor(
    savedState: SavedStateHandle,
    publishedBooksRepo: IPublishedBooksRepo,
    private val getBookIdFromCompoundId: GetBookIdFromCompoundId
): ViewModel() {
    private val title =  savedState.get<String>(Book.NAME)!!
    private val bookId =   savedState.get<String>(Book.ID)!!

    private var localLivePublishedBook: LiveData<Optional<PublishedBook>> = MutableLiveData()

    init {
        localLivePublishedBook = publishedBooksRepo.getALiveOptionalPublishedBook(bookId)
    }

    fun getLivePublishedBook(): LiveData<Optional<PublishedBook>> {
        return localLivePublishedBook
    }

    fun getTitle(): String {
        return title
    }

    fun getBookIdFromCompoundId(): String {
        return getBookIdFromCompoundId.invoke(bookId)
    }

}