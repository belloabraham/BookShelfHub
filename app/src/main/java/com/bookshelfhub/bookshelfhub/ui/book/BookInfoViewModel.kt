package com.bookshelfhub.bookshelfhub.ui.book

import androidx.lifecycle.*
import com.bookshelfhub.bookshelfhub.data.models.entities.PublishedBook
import com.bookshelfhub.bookshelfhub.data.Book
import com.bookshelfhub.bookshelfhub.data.repos.PublishedBooksRepo
import com.google.common.base.Optional
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BookInfoViewModel @Inject constructor(
  publishedBooksRepo: PublishedBooksRepo,
  val savedState: SavedStateHandle): ViewModel(){

  private var localLivePublishedBook: LiveData<Optional<PublishedBook>> = MutableLiveData()

  private val isbn = savedState.get<String>(Book.ID)!!

  init {
    localLivePublishedBook = publishedBooksRepo.getALiveOptionalPublishedBook(isbn)
  }

  fun getLivePublishedBook(): LiveData<Optional<PublishedBook>> {
    return localLivePublishedBook
  }

}