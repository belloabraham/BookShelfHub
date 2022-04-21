package com.bookshelfhub.bookshelfhub

import android.net.Uri
import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import com.bookshelfhub.bookshelfhub.helpers.utils.datetime.DateTimeUtil
import com.bookshelfhub.bookshelfhub.helpers.utils.settings.Settings
import com.bookshelfhub.bookshelfhub.helpers.utils.settings.SettingsUtil
import com.bookshelfhub.bookshelfhub.data.models.entities.*
import com.bookshelfhub.bookshelfhub.data.Book
import com.bookshelfhub.bookshelfhub.data.models.apis.convertion.Fixer
import com.bookshelfhub.bookshelfhub.data.repos.*
import com.bookshelfhub.bookshelfhub.extensions.containsUrl
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.helpers.dynamiclink.IDynamicLink
import com.bookshelfhub.bookshelfhub.helpers.utils.Regex
import com.bookshelfhub.bookshelfhub.helpers.webapi.currencyconverter.CurrencyConversionAPI
import com.bookshelfhub.bookshelfhub.workers.Constraint
import com.bookshelfhub.bookshelfhub.workers.PostUserReview
import com.bookshelfhub.bookshelfhub.workers.Worker
import com.google.common.base.Optional
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class BookItemActivityViewModel @Inject constructor(
  val settingsUtil: SettingsUtil,
  val savedState: SavedStateHandle,
  private val publishedBooksRepo: PublishedBooksRepo,
  private  val userReviewRepo: UserReviewRepo,
  orderedBooksRepo: OrderedBooksRepo,
  private val cartItemsRepo:CartItemsRepo,
  private val dynamicLink:IDynamicLink,
  private val userRepo: UserRepo,
  private val currencyConversionAPI: CurrencyConversionAPI,
  private val worker: Worker,
  referralRepo: ReferralRepo,
  private val searchHistoryRepo: SearchHistoryRepo,
  userAuth: IUserAuth): ViewModel(){

  private var liveCartItems: LiveData<List<CartItem>> = MutableLiveData()
  private var userReviews: MutableLiveData<List<UserReview>> = MutableLiveData()
  private var liveUserReview: LiveData<Optional<UserReview>> = MutableLiveData()
  private var publishedBook: MutableLiveData<PublishedBook> = MutableLiveData()
  private var localLivePublishedBook: LiveData<Optional<PublishedBook>> = MutableLiveData()
  private var publisherReferrer: LiveData<Optional<Collaborator>> = MutableLiveData()
  private var orderedBook: LiveData<Optional<OrderedBook>> = MutableLiveData()
  private lateinit var user:User


  private val userId = userAuth.getUserId()
  private val title = savedState.get<String>(Book.NAME)!!
  private val author = savedState.get<String>(Book.AUTHOR)!!
  private val bookId = savedState.get<String>(Book.ID)!!
  private val isSearchItem = savedState.get<Boolean>(Book.IS_SEARCH_ITEM)?:false
  private var bookShareUrl: Uri? = null

  private val config  = PagingConfig(
    pageSize = 5,
    enablePlaceholders = true,
    initialLoadSize = 10
  )

  init {

    generateBookShareLink()

    localLivePublishedBook = publishedBooksRepo.getALiveOptionalPublishedBook(bookId)

    publisherReferrer = referralRepo.getALiveOptionalCollaborator(bookId)

    liveCartItems = cartItemsRepo.getLiveListOfCartItems(userId)

    orderedBook = orderedBooksRepo.getALiveOptionalOrderedBook(bookId)

    //This way this code does not get recalled if activity restarts
    viewModelScope.launch{
      user = userRepo.getUser(userId).get()
      liveUserReview = userReviewRepo.getLiveUserReview(bookId, userId)
    }


    viewModelScope.launch {
      try {
        publishedBook.value = publishedBooksRepo.getARemotePublishedBook(bookId)
      }catch (e:Exception){
        Timber.e(e)
        return@launch
      }
    }



    viewModelScope.launch {
      try {
        userReviews.value  = userReviewRepo.getListOfBookReviews(bookId, 3, excludedDocId =  userId)
      }catch (e:Exception){
        Timber.e(e)
        return@launch
      }
    }


    //Check if this activity was started by a search result adapter item in StoreFragment, if so record a search history
    //for store fragment search result
    if (isSearchItem){
      addStoreSearchHistory(StoreSearchHistory(bookId, title, userAuth.getUserId(), author, DateTimeUtil.getDateTimeAsString()))
    }


  }

  private fun generateBookShareLink(){
    viewModelScope.launch {
        val book = publishedBooksRepo.getPublishedBook(bookId).get()
        try {
          bookShareUrl = dynamicLink.generateShortLinkAsync(book.name , book.description, book.coverUrl, userId)
        }catch (e:Exception){
          Timber.e(e)
          return@launch
        }
    }
  }

  fun getBookShareLink(): Uri? {
    return bookShareUrl
  }



  suspend fun convertCurrency(fromCurrency:String, toCurrency:String, amount:Double): Response<Fixer> {
    val fixerAccessKey =  settingsUtil.getString(Settings.FIXER_ACCESS_KEY)!!
    return currencyConversionAPI.convert(fixerAccessKey, fromCurrency, toCurrency, amount)
  }

   fun getLiveUserReview(): LiveData<Optional<UserReview>> {
    return liveUserReview
  }

  fun getIsbn():String{
    return  bookId
  }

  fun getALiveOrderedBook(): LiveData<Optional<OrderedBook>> {
    return orderedBook
  }

  fun getLivePubReferrerByIsbn(): LiveData<Optional<Collaborator>> {
    return publisherReferrer
  }

  fun addToCart(cart: CartItem){
    viewModelScope.launch{
      cartItemsRepo.addToCart(cart)
    }
  }

  fun getUser(): User {
    return user
  }

  fun getLiveLocalBook(): LiveData<Optional<PublishedBook>> {
    return localLivePublishedBook
  }

  fun addUserReview(userReview: UserReview, diffInRating:Double){
    viewModelScope.launch{
      userReviewRepo.addUserReview(userReview)
      val isSpamUrlInReview = !userReview.review.containsUrl(Regex.WEB_LINK_IN_TEXT)
      if (isSpamUrlInReview){
        //Put data to be passed to the review worker, data of the BOOK_ID(Book that was reviewed) and Rating diff
        val data = Data.Builder()
        data.putString(Book.ID, userReview.bookId)
        data.putDouble(Book.RATING_DIFF, diffInRating)

        val userReviewPostWorker =
          OneTimeWorkRequestBuilder<PostUserReview>()
            .setConstraints(Constraint.getConnected())
            .setInputData(data.build())
            .build()
        worker.enqueue(userReviewPostWorker)
      }
    }
  }

  private fun addStoreSearchHistory(storeSearchHistory: StoreSearchHistory){
    viewModelScope.launch {
      searchHistoryRepo.addStoreSearchHistory(storeSearchHistory)
    }
  }

  fun getTwoUserReviewsForBook(): LiveData<List<UserReview>> {
    return userReviews
  }


  fun getPublishedBookOnline(): LiveData<PublishedBook> {
    return publishedBook
  }

  fun getLiveListOfCartItems(): LiveData<List<CartItem>> {
    return liveCartItems
  }

  fun getBooksByCategoryPageSource(category:String): Flow<PagingData<PublishedBook>> {
   return Pager(config){
      publishedBooksRepo.getBooksByCategoryPageSource(category)
    }.flow
  }

}