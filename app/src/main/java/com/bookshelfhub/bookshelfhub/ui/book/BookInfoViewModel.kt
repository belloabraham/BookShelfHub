package com.bookshelfhub.bookshelfhub.ui.book

import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.bookshelfhub.bookshelfhub.enums.Book
import com.bookshelfhub.bookshelfhub.enums.DbFields
import com.bookshelfhub.bookshelfhub.services.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.services.database.cloud.ICloudDb
import com.bookshelfhub.bookshelfhub.services.database.local.ILocalDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.*
import com.google.common.base.Optional
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
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