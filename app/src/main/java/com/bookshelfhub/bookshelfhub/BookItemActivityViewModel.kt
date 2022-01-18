package com.bookshelfhub.bookshelfhub

import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.bookshelfhub.bookshelfhub.Utils.datetime.DateTimeUtil
import com.bookshelfhub.bookshelfhub.Utils.settings.Settings
import com.bookshelfhub.bookshelfhub.Utils.settings.SettingsUtil
import com.bookshelfhub.bookshelfhub.enums.Book
import com.bookshelfhub.bookshelfhub.services.database.cloud.DbFields
import com.bookshelfhub.bookshelfhub.services.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.services.database.cloud.ICloudDb
import com.bookshelfhub.bookshelfhub.helpers.database.ILocalDb
import com.bookshelfhub.bookshelfhub.helpers.database.room.entities.*
import com.google.common.base.Optional
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookItemActivityViewModel @Inject constructor(
  private val localDb: ILocalDb,
  val cloudDb:ICloudDb,
  val settingsUtil: SettingsUtil,
  val savedState: SavedStateHandle,
  userAuth: IUserAuth): ViewModel(){

  private var liveCartItems: LiveData<List<Cart>> = MutableLiveData()
  private var userReviews: MutableLiveData<List<UserReview>> = MutableLiveData()
  private var liveUserReview: LiveData<Optional<UserReview>> = MutableLiveData()
  private var publishedBook: MutableLiveData<PublishedBook> = MutableLiveData()
  private var localLivePublishedBook: LiveData<Optional<PublishedBook>> = MutableLiveData()
  private var publisherReferrer: LiveData<Optional<PubReferrers>> = MutableLiveData()
  private var orderedBook: LiveData<Optional<OrderedBooks>> = MutableLiveData()


  private val userId = userAuth.getUserId()
  private val title = savedState.get<String>(Book.NAME.KEY)!!
  private val author = savedState.get<String>(Book.AUTHOR.KEY)!!
  private val isbn = savedState.get<String>(Book.ISBN.KEY)!!
  private val isSearchItem = savedState.get<Boolean>(Book.IS_SEARCH_ITEM.KEY)?:false
  private var conversionEndPoint:String?=null


  private val config  = PagingConfig(
    pageSize = 5,
    enablePlaceholders = true,
    initialLoadSize = 10
  )

  init {
    localLivePublishedBook = localDb.getLivePublishedBook(isbn)

    liveUserReview = localDb.getLiveUserReview(isbn)

    publisherReferrer = localDb.getLivePubReferrer(isbn)

    liveCartItems = localDb.getLiveListOfCartItems(userId)

    orderedBook = localDb.getALiveOrderedBook(isbn)

    viewModelScope.launch(IO) {
       conversionEndPoint =  settingsUtil.getString(Settings.FIXER_ENDPOINT.KEY)
    }

    cloudDb.getLiveDataAsync(
      DbFields.PUBLISHED_BOOKS.KEY, isbn,
      PublishedBook::class.java){
      publishedBook.value = it
    }

    cloudDb.getListOfDataWhereAsync(DbFields.PUBLISHED_BOOKS.KEY, isbn, DbFields.REVIEWS.KEY, UserReview::class.java, DbFields.VERIFIED.KEY, whereValue = true, userId, limit = 3){ reviews, e->
      userReviews.value = reviews
    }

    //Check if this activity was started by a search result adapter item in StoreFragment, if so record a search history
    //for store fragment search result
    if (isSearchItem){
      addStoreSearchHistory(StoreSearchHistory(isbn, title, userAuth.getUserId(), author, DateTimeUtil.getDateTimeAsString()))
    }

  }

  fun getConversionEndPoint(): String? {
    return conversionEndPoint
  }

  fun getIsbn():String{
    return  isbn
  }

  fun getALiveOrderedBook(): LiveData<Optional<OrderedBooks>> {
    return orderedBook
  }

  fun getLivePubReferrerByIsbn(): LiveData<Optional<PubReferrers>> {
    return publisherReferrer
  }

  fun addToCart(cart: Cart){
    viewModelScope.launch(IO){
      localDb.addToCart(cart)
    }
  }

  fun getLiveLocalBook(): LiveData<Optional<PublishedBook>> {
    return localLivePublishedBook
  }

  fun addUserReview(userReview: UserReview){
    viewModelScope.launch(IO){
      localDb.addUserReview(userReview)
    }
  }

  private fun addStoreSearchHistory(storeSearchHistory: StoreSearchHistory){
    viewModelScope.launch(IO) {
      localDb.addStoreSearchHistory(storeSearchHistory)
    }
  }

  fun getTopThreeOnlineUserReviews(): LiveData<List<UserReview>> {
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