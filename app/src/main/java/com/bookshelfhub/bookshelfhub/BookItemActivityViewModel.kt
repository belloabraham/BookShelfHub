package com.bookshelfhub.bookshelfhub

import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.bookshelfhub.bookshelfhub.helpers.utils.datetime.DateTimeUtil
import com.bookshelfhub.bookshelfhub.helpers.utils.settings.Settings
import com.bookshelfhub.bookshelfhub.helpers.utils.settings.SettingsUtil
import com.bookshelfhub.bookshelfhub.data.models.entities.*
import com.bookshelfhub.bookshelfhub.data.enums.Book
import com.bookshelfhub.bookshelfhub.data.repos.*
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.RemoteDataFields
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import com.google.common.base.Optional
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookItemActivityViewModel @Inject constructor(
    val settingsUtil: SettingsUtil,
    val savedState: SavedStateHandle,
    private val publishedBooksRepo: PublishedBooksRepo,
    private  val userReviewRepo: UserReviewRepo,
    orderedBooksRepo: OrderedBooksRepo,
    private val cartItemsRepo:CartItemsRepo,
    referralRepo: ReferralRepo,
    private val searchHistoryRepo: SearchHistoryRepo,
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
  private val bookId = savedState.get<String>(Book.ISBN.KEY)!!
  private val isSearchItem = savedState.get<Boolean>(Book.IS_SEARCH_ITEM.KEY)?:false
  private var conversionEndPoint:String?=null


  private val config  = PagingConfig(
    pageSize = 5,
    enablePlaceholders = true,
    initialLoadSize = 10
  )

  init {
    localLivePublishedBook = publishedBooksRepo.getLivePublishedBook(bookId)

    liveUserReview = userReviewRepo.getLiveUserReview(bookId)

    publisherReferrer = referralRepo.getLivePubReferrer(bookId)

    liveCartItems = cartItemsRepo.getLiveListOfCartItems(userId)

    orderedBook = orderedBooksRepo.getALiveOrderedBook(bookId)

    viewModelScope.launch{
       conversionEndPoint =  settingsUtil.getString(Settings.FIXER_ENDPOINT.KEY)
    }

    publishedBooksRepo.getALiveRemotePublishedBook(bookId){
      publishedBook.value = it
    }

    publishedBooksRepo.getTopThreeRemoteBookReviews(bookId, userId){ userReviews, e->
      this.userReviews.value = userReviews
    }

    //Check if this activity was started by a search result adapter item in StoreFragment, if so record a search history
    //for store fragment search result
    if (isSearchItem){
      addStoreSearchHistory(StoreSearchHistory(bookId, title, userAuth.getUserId(), author, DateTimeUtil.getDateTimeAsString()))
    }

  }

  fun getConversionEndPoint(): String? {
    return conversionEndPoint
  }

  fun getIsbn():String{
    return  bookId
  }

  fun getALiveOrderedBook(): LiveData<Optional<OrderedBooks>> {
    return orderedBook
  }

  fun getLivePubReferrerByIsbn(): LiveData<Optional<PubReferrers>> {
    return publisherReferrer
  }

  fun addToCart(cart: Cart){
    viewModelScope.launch{
      cartItemsRepo.addToCart(cart)
    }
  }

  fun getLiveLocalBook(): LiveData<Optional<PublishedBook>> {
    return localLivePublishedBook
  }

  fun addUserReview(userReview: UserReview){
    viewModelScope.launch{
      userReviewRepo.addUserReview(userReview)
    }
  }

  private fun addStoreSearchHistory(storeSearchHistory: StoreSearchHistory){
    viewModelScope.launch {
      searchHistoryRepo.addStoreSearchHistory(storeSearchHistory)
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
      publishedBooksRepo.getBooksByCategoryPageSource(category)
    }.flow
  }

}