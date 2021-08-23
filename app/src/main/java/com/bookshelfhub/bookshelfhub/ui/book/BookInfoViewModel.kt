package com.bookshelfhub.bookshelfhub.ui.book

import androidx.lifecycle.*
import com.bookshelfhub.bookshelfhub.book.Book
import com.bookshelfhub.bookshelfhub.services.database.local.ILocalDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BookInfoViewModel @Inject constructor(
   localDb: ILocalDb,
  val savedState: SavedStateHandle): ViewModel(){

  private var localLivePublishedBook: LiveData<PublishedBook> = MutableLiveData()

  val isbn = savedState.get<String>(Book.ISBN.KEY)!!

  init {
    localLivePublishedBook = localDb.getLivePublishedBook(isbn)
  }

  fun getLivePublishedBook(): LiveData<PublishedBook> {
    return localLivePublishedBook
  }

}