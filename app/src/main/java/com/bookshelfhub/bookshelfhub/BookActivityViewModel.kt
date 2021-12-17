package com.bookshelfhub.bookshelfhub

import androidx.lifecycle.*
import com.bookshelfhub.bookshelfhub.enums.Book
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.BookVideos
import com.bookshelfhub.bookshelfhub.services.database.cloud.DbFields
import com.bookshelfhub.bookshelfhub.services.database.cloud.ICloudDb
import com.bookshelfhub.bookshelfhub.services.database.local.ILocalDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.*
import com.google.common.base.Optional
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.Job as Job

@HiltViewModel
class BookActivityViewModel @Inject constructor(
  private val localDb: ILocalDb,
  private val cloudDb: ICloudDb,
  val savedState: SavedStateHandle
  ): ViewModel(){

  val isbn = savedState.get<String>(Book.ISBN.KEY)!!
  private var livePublishedBook: LiveData<Optional<PublishedBook>> = MutableLiveData()


  init {
      livePublishedBook = localDb.getLivePublishedBook(isbn)
      cloudDb.getLiveListOfDataAsync(
        DbFields.PUBLISHED_BOOKS.KEY, isbn, DbFields.VIDEO_LIST.KEY,BookVideos::class.java, true
      ){
        if (it.isNotEmpty()){
          viewModelScope.launch(IO) {
            localDb.addBookVideos(it)
          }
        }
      }
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
  fun getLivePublishedBook(): LiveData<Optional<PublishedBook>> {
    return livePublishedBook
  }

  suspend fun getAnOrderedBook(): OrderedBooks {
   return localDb.getAnOrderedBook(isbn)
  }


  fun getLiveListOfBookVideos(): LiveData<List<BookVideos>> {
    return localDb.getLiveListOfBookVideos(isbn)
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