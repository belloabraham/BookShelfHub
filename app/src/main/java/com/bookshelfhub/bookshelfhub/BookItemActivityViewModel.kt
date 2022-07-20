package com.bookshelfhub.bookshelfhub

import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import com.bookshelfhub.bookshelfhub.helpers.utils.datetime.DateTimeUtil
import com.bookshelfhub.bookshelfhub.helpers.settings.Settings
import com.bookshelfhub.bookshelfhub.helpers.settings.SettingsUtil
import com.bookshelfhub.bookshelfhub.data.models.entities.*
import com.bookshelfhub.bookshelfhub.data.Book
import com.bookshelfhub.bookshelfhub.data.models.apis.convertion.Fixer
import com.bookshelfhub.bookshelfhub.data.models.uistate.BookDownloadState
import com.bookshelfhub.bookshelfhub.data.models.uistate.PublishedBookUiState
import com.bookshelfhub.bookshelfhub.data.repos.bookdownload.IBookDownloadStateRepo
import com.bookshelfhub.bookshelfhub.data.repos.cartitems.ICartItemsRepo
import com.bookshelfhub.bookshelfhub.data.repos.orderedbooks.IOrderedBooksRepo
import com.bookshelfhub.bookshelfhub.data.repos.privatekeys.IPrivateKeysRepo
import com.bookshelfhub.bookshelfhub.data.repos.privatekeys.PrivateKeysRepo
import com.bookshelfhub.bookshelfhub.data.repos.publishedbooks.IPublishedBooksRepo
import com.bookshelfhub.bookshelfhub.data.repos.referral.IReferralRepo
import com.bookshelfhub.bookshelfhub.data.repos.searchhistory.ISearchHistoryRepo
import com.bookshelfhub.bookshelfhub.data.repos.user.IUserRepo
import com.bookshelfhub.bookshelfhub.data.repos.userreview.IUserReviewRepo
import com.bookshelfhub.bookshelfhub.domain.usecases.DownloadBookUseCase
import com.bookshelfhub.bookshelfhub.domain.usecases.GetBookIdFromCompoundId
import com.bookshelfhub.bookshelfhub.extensions.containsUrl
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.helpers.dynamiclink.IDynamicLink
import com.bookshelfhub.bookshelfhub.helpers.utils.Regex
import com.bookshelfhub.bookshelfhub.helpers.webapi.currencyconverter.ICurrencyConversionAPI
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
  private val publishedBooksRepo: IPublishedBooksRepo,
  private  val userReviewRepo: IUserReviewRepo,
  private val orderedBooksRepo: IOrderedBooksRepo,
  private val cartItemsRepo: ICartItemsRepo,
  private val dynamicLink:IDynamicLink,
  private  val privateKeysRepo: IPrivateKeysRepo,
  private val userRepo: IUserRepo,
  private val currencyConversionAPI: ICurrencyConversionAPI,
  private val worker: Worker,
  private val getBookIdFromCompoundId: GetBookIdFromCompoundId,
  private val downloadBookUseCase: DownloadBookUseCase,
  private val referralRepo: IReferralRepo,
  private val bookDownloadStateRepo: IBookDownloadStateRepo,
  private val searchHistoryRepo: ISearchHistoryRepo,
  userAuth: IUserAuth): ViewModel(){

  private var liveCartItems: LiveData<List<CartItem>> = MutableLiveData()
  private var userReviews: MutableLiveData<List<UserReview>> = MutableLiveData()
  private var liveUserReview: LiveData<Optional<UserReview>> = MutableLiveData()
  private var publishedBookOnline: MutableLiveData<PublishedBook> = MutableLiveData()
  private var orderedBook: LiveData<Optional<OrderedBook>> = MutableLiveData()
  private lateinit var user:User
  private var userAlreadyPurchasedBook = false


  private val userId = userAuth.getUserId()
  private val title = savedState.get<String>(Book.NAME)!!
  private val author = savedState.get<String>(Book.AUTHOR)!!
  private val bookId = savedState.get<String>(Book.ID)!!
  private val isSearchItem = savedState.get<Boolean>(Book.IS_SEARCH_ITEM)?:false
  private var bookShareUrl: String? = null

  private val config  = PagingConfig(
    pageSize = 5,
    enablePlaceholders = true,
    initialLoadSize = 10
  )

  init {

    generateBookShareLink()

    liveCartItems = cartItemsRepo.getLiveListOfCartItems(userId)

    orderedBook = orderedBooksRepo.getALiveOptionalOrderedBook(bookId)

    viewModelScope.launch{
      userAlreadyPurchasedBook = orderedBooksRepo.getAnOrderedBook(bookId).isPresent
      user = userRepo.getUser(userId).get()
      liveUserReview = userReviewRepo.getLiveUserReview(bookId, userId)
    }


    viewModelScope.launch {
      if(!userAlreadyPurchasedBook){
        try {
          publishedBookOnline.value = publishedBooksRepo.getARemotePublishedBook(bookId)
        }catch (e:Exception){
          Timber.e(e)
          return@launch
        }
      }

    }

    viewModelScope.launch {
      try {
        userReviews.value  = userReviewRepo.getRemoteListOfBookReviews(bookId, 3, excludedDocId =  userId)
      }catch (e:Exception){
        Timber.e(e)
        return@launch
      }
    }

    val searchHistory = StoreSearchHistory(
      bookId, title, userAuth.getUserId(),
      author, DateTimeUtil.getDateTimeAsString()
    )

    if (isSearchItem){
      addStoreSearchHistory(searchHistory)
    }
  }

  suspend fun getTotalNoOfOrderedBooks(): Int {
    return orderedBooksRepo.getTotalNoOfOrderedBooks()
  }

  suspend fun getAnOrderedBook(): Optional<OrderedBook> {
    return orderedBooksRepo.getAnOrderedBook(bookId)
  }

  fun deleteDownloadState(bookDownloadState: BookDownloadState){
    viewModelScope.launch {
      bookDownloadStateRepo.deleteDownloadState(bookDownloadState)
    }
  }

  fun getLiveBookDownloadState(bookId:String): LiveData<Optional<BookDownloadState>> {
    return bookDownloadStateRepo.getLiveBookDownloadState(bookId)
  }

  fun startBookDownload(workData: Data){

    viewModelScope.launch {
      downloadBookUseCase(worker, workData, bookDownloadStateRepo)
    }
  }

  fun getBookAlreadyPurchasedByUser(): Boolean {
    return userAlreadyPurchasedBook
  }

  suspend fun getLocalPublishedBook(): Optional<PublishedBook> {
    return publishedBooksRepo.getPublishedBook(bookId)
  }

  suspend fun getOptionalOrderedBook(bookId:String): Optional<OrderedBook> {
    return orderedBooksRepo.getAnOrderedBook(bookId)
  }

  suspend fun getCollaboratorIdForThisBook(): String? {
    val collaborator = referralRepo.getAnOptionalCollaborator(bookId)
    return if(collaborator.isPresent) collaborator.get().collabId else null
  }

  private fun generateBookShareLink(){
    viewModelScope.launch {
      bookShareUrl = settingsUtil.getString(bookId)
      if (bookShareUrl == null){
        val book = publishedBooksRepo.getPublishedBook(bookId).get()
        try {
          bookShareUrl = dynamicLink.generateShortDynamicLinkAsync(
            book.name , book.description, book.coverUrl, userId).toString()
          settingsUtil.setString(bookId, bookShareUrl!!.toString())
        }catch (e:Exception){
          Timber.e(e)
          return@launch
        }
      }
    }
  }

  fun getBookShareLink(): String? {
    return bookShareUrl
  }

  fun addAnOrderedBook(orderedBook: OrderedBook){
    viewModelScope.launch {
      orderedBooksRepo.addAnOrderedBook(orderedBook)
    }
  }

  suspend fun convertCurrency(fromCurrency:String, toCurrency:String, amount:Double): Response<Fixer> {
    val fixerAccessKey =  settingsUtil.getString(Settings.FIXER_ACCESS_KEY)

    return if(fixerAccessKey == null){
      val apiKeys  = privateKeysRepo.getPrivateKeys(Settings.API_KEYS, ApiKeys::class.java)!!

          settingsUtil.setString(
            Settings.PERSPECTIVE_API,
            apiKeys.perspectiveKey!!
          )
          settingsUtil.setString(
            Settings.FIXER_ACCESS_KEY,
            apiKeys.fixerAccessKey!!
          )

          settingsUtil.setString(
            Settings.PAYSTACK_LIVE_PUBLIC_KEY,
            apiKeys.payStackLivePublicKey!!
          )

      currencyConversionAPI.convert(apiKeys.fixerAccessKey!!, fromCurrency, toCurrency, amount)
    }else{
       currencyConversionAPI.convert(fixerAccessKey, fromCurrency, toCurrency, amount)
    }
  }

   fun getLiveUserReview(): LiveData<Optional<UserReview>> {
    return liveUserReview
  }

  suspend fun getAllOrderedBooks(): List<OrderedBook> {
    return orderedBooksRepo.getOrderedBooks(userId)
  }

  fun getBookIdFromPossiblyMergedIds(possiblyMergedIds:String): String {
   return getBookIdFromCompoundId(possiblyMergedIds)
  }

  fun getBookId():String{
    return  bookId
  }

  fun getALiveOrderedBook(): LiveData<Optional<OrderedBook>> {
    return orderedBook
  }


  fun addToCart(cart: CartItem){
    viewModelScope.launch{
      cartItemsRepo.addToCart(cart)
    }
  }

  fun getUser(): User {
    return user
  }

  fun addUserReview(userReview: UserReview, diffInRating:Double){
    viewModelScope.launch{
      userReviewRepo.addUserReview(userReview)
      val isSpamUrlInReview = !userReview.review.containsUrl(Regex.WEB_LINK_IN_TEXT)
      if (isSpamUrlInReview){

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

  fun getBookRemotelyIfNotPurchased(): LiveData<PublishedBook> {
    return publishedBookOnline
  }

  fun getLiveListOfCartItems(): LiveData<List<CartItem>> {
    return liveCartItems
  }

  fun getSimilarBooksByCategoryPageSource(category:String, bookId: String): Flow<PagingData<PublishedBookUiState>> {
    return Pager(config){
      publishedBooksRepo.getSimilarBooksByCategoryPageSource(category, bookId)
    }.flow
  }

  fun getBooksByCategoryPageSource(category:String): Flow<PagingData<PublishedBookUiState>> {
   return Pager(config){
      publishedBooksRepo.getBooksByCategoryPageSource(category)
    }.flow
  }

}