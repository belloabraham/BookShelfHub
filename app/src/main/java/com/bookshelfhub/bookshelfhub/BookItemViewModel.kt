package com.bookshelfhub.bookshelfhub

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
class BookItemViewModel @Inject constructor(
  private val localDb: ILocalDb,
  val cloudDb:ICloudDb,
  val savedState: SavedStateHandle,
  userAuth: IUserAuth): ViewModel(){

  private var liveCartItems: LiveData<List<Cart>> = MutableLiveData()
  private var userReviews: MutableLiveData<List<UserReview>> = MutableLiveData()
  private var liveUserReview: LiveData<Optional<UserReview>> = MutableLiveData()
  private var publishedBook: MutableLiveData<PublishedBook> = MutableLiveData()
  private var localLivePublishedBook: LiveData<PublishedBook> = MutableLiveData()

  val userId = userAuth.getUserId()
  val isbn = savedState.get<String>(Book.ISBN.KEY)!!

  private val config  = PagingConfig(
    pageSize = 5,
    enablePlaceholders = true,
    initialLoadSize = 10
  )

  init {
    liveCartItems = localDb.getLiveListOfCartItems(userId)
    liveUserReview = localDb.getLiveUserReview(isbn)

    localLivePublishedBook = localDb.getLivePublishedBook(isbn)

    cloudDb.getLiveDataAsync(
      DbFields.PUBLISHED_BOOKS.KEY, isbn,
      PublishedBook::class.java){
      publishedBook.value = it
    }

    cloudDb.getListOfDataAsync(DbFields.PUBLISHED_BOOKS.KEY, isbn, DbFields.REVIEWS.KEY, UserReview::class.java, DbFields.VERIFIED.KEY, whereValue = true, userId, limit = 3){ reviews, e->
      userReviews.value = reviews
    }

  }

  fun getLivePublishedBook(): LiveData<PublishedBook> {
    return localLivePublishedBook
  }

  fun addUserReview(userReview: UserReview){
    viewModelScope.launch(IO){
      localDb.addUserReview(userReview)
    }
  }

  fun addStoreSearchHistory(storeSearchHistory: StoreSearchHistory){
    viewModelScope.launch(IO) {
      localDb.addStoreSearchHistory(storeSearchHistory)
    }
  }

  fun getUserReviews(): LiveData<List<UserReview>> {
    return userReviews
  }


  fun getPublishedBookOnline(): LiveData<PublishedBook> {
    return publishedBook
  }

  fun getLiveListOfCartItems(): LiveData<List<Cart>> {
    return liveCartItems
  }

  fun getLiveUserReview(): LiveData<Optional<UserReview>> {
    return liveUserReview
  }

  fun getBooksByCategoryPageSource(category:String): Flow<PagingData<PublishedBook>> {
    return Pager(config){
      localDb.getBooksByCategoryPageSource(category)
    }.flow
  }

}