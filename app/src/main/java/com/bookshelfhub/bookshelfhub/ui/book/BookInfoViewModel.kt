package com.bookshelfhub.bookshelfhub.ui.book

import androidx.lifecycle.*
import com.bookshelfhub.bookshelfhub.data.models.entities.PublishedBook
import com.bookshelfhub.bookshelfhub.data.enums.Book
import com.bookshelfhub.bookshelfhub.helpers.database.ILocalDb
import com.google.common.base.Optional
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BookInfoViewModel @Inject constructor(
  localDb: ILocalDb,
  val savedState: SavedStateHandle): ViewModel(){

  private var localLivePublishedBook: LiveData<Optional<PublishedBook>> = MutableLiveData()

  private val isbn = savedState.get<String>(Book.ISBN.KEY)!!

  init {
    localLivePublishedBook = localDb.getLivePublishedBook(isbn)
  }

  fun getLivePublishedBook(): LiveData<Optional<PublishedBook>> {
    return localLivePublishedBook
  }

}