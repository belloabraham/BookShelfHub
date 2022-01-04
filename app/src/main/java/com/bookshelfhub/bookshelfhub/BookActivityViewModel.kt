package com.bookshelfhub.bookshelfhub

import androidx.lifecycle.*
import com.bookshelfhub.bookshelfhub.Utils.datetime.DateTimeUtil
import com.bookshelfhub.bookshelfhub.enums.Book
import com.bookshelfhub.bookshelfhub.services.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.helpers.database.room.entities.BookVideos
import com.bookshelfhub.bookshelfhub.services.database.cloud.DbFields
import com.bookshelfhub.bookshelfhub.services.database.cloud.ICloudDb
import com.bookshelfhub.bookshelfhub.helpers.database.ILocalDb
import com.bookshelfhub.bookshelfhub.helpers.database.room.entities.*
import com.google.common.base.Optional
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookActivityViewModel @Inject constructor(
  val cloudDb: ICloudDb,
  val userAuth:IUserAuth,
  private val localDb: ILocalDb,
  val savedState: SavedStateHandle
  ): ViewModel(){

  val userId = userAuth.getUserId()
  val isbn = savedState.get<String>(Book.ISBN.KEY)!!
  val title = savedState.get<String>(Book.TITLE.KEY)!!
  private val isSearchItem = savedState.get<Boolean>(Book.IS_SEARCH_ITEM.KEY)?:false


  private var livePublishedBook: LiveData<Optional<PublishedBook>> = MutableLiveData()
  private lateinit var orderedBook:OrderedBooks

  init {

      deleteAllHistory()
      livePublishedBook = localDb.getLivePublishedBook(isbn)
      viewModelScope.launch(IO){
        orderedBook = localDb.getAnOrderedBook(isbn)
      }

      cloudDb.getLiveListOfDataWhereAsync(
        DbFields.PUBLISHED_BOOKS.KEY, isbn, DbFields.VIDEO_LIST.KEY,BookVideos::class.java, true
      ){
        if (it.isNotEmpty()){
          viewModelScope.launch(IO) {
            localDb.addBookVideos(it)
          }
        }
      }
    if (isSearchItem){
     addShelfSearchHistory(ShelfSearchHistory(isbn, title, userId, DateTimeUtil.getDateTimeAsString()))
    }
  }

  fun getIsbnNo(): String {
    return isbn
  }

  fun getBookTitle(): String {
        return title
  }

  fun deleteFromBookmark(pageNumb:Int){
    viewModelScope.launch(IO){
      localDb.deleteFromBookmark(pageNumb, isbn)
    }
  }

  suspend fun getBookmark(currentPage:Int): Optional<Bookmark> {
    return localDb.getBookmark(currentPage, isbn)
  }

  fun addBookmark(pageNumber: Int){
    val bookmark = Bookmark(userId, isbn, pageNumber, title)
    viewModelScope.launch(IO){
      localDb.addBookmark(bookmark)
    }
  }

  fun getLivePublishedBook(): LiveData<Optional<PublishedBook>> {
    return livePublishedBook
  }

   fun getAnOrderedBook(): OrderedBooks {
   return orderedBook
  }


  fun getLiveListOfBookVideos(): LiveData<List<BookVideos>> {
    return localDb.getLiveListOfBookVideos(isbn)
  }

  private fun deleteAllHistory(){
    viewModelScope.launch(IO){
      localDb.deleteAllHistory()
    }
  }

  private fun addShelfSearchHistory(shelfSearchHistory: ShelfSearchHistory){
    viewModelScope.launch(IO){
      localDb.addShelfSearchHistory(shelfSearchHistory)
    }
  }

  fun addReadHistory(pageNumber:Int, percentage:Int){
    val readHistory = History(isbn, pageNumber, percentage, title)
    viewModelScope.launch(IO){
      localDb.addReadHistory(readHistory)
    }
  }

}