package com.bookshelfhub.bookshelfhub

import androidx.lifecycle.*
import com.bookshelfhub.bookshelfhub.book.Book
import com.bookshelfhub.bookshelfhub.services.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.services.database.local.ILocalDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookActivityViewModel @Inject constructor(
  private val localDb: ILocalDb,
  val savedState: SavedStateHandle
  ): ViewModel(){

  val isbn = savedState.get<String>(Book.ISBN.KEY)!!
  private var liveOrderedBook: LiveData<OrderedBooks> = MutableLiveData()
  private var livePublishedBook: LiveData<PublishedBook> = MutableLiveData()


  init {
      liveOrderedBook = localDb.getLiveOrderedBook(isbn)
      livePublishedBook = localDb.getLivePublishedBook(isbn)
  }

  fun deleteFromBookmark(pageNumb:Int, isbn:String){
    viewModelScope.launch(IO){
      localDb.deleteFromBookmark(pageNumb, isbn)
    }
  }

  fun addBookmark(bookmark: Bookmark){
    viewModelScope.launch(IO){
      localDb.addBookmark(bookmark)
    }
  }
  fun getLivePublishedBook(): LiveData<PublishedBook> {
    return livePublishedBook
  }

  fun getLiveOrderedBook(): LiveData<OrderedBooks> {
    return liveOrderedBook
  }

  fun deleteAllHistory(){
    viewModelScope.launch(IO){
      localDb.deleteAllHistory()
    }
  }

  fun addShelfSearchHistory(shelfSearchHistory: ShelfSearchHistory){
    viewModelScope.launch(IO){
      localDb.addShelfSearchHistory(shelfSearchHistory)
    }
  }

  fun addReadHistory(readHistory: History){
    viewModelScope.launch(IO){
      localDb.addReadHistory(readHistory)
    }
  }


}