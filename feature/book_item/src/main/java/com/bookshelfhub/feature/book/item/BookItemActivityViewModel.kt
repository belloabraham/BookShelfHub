package com.bookshelfhub.feature.book.item

import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.work.*
import com.bookshelfhub.core.authentication.IUserAuth
import com.bookshelfhub.core.common.extensions.containsUrl
import com.bookshelfhub.core.common.helpers.ErrorUtil
import com.bookshelfhub.core.common.worker.Constraint
import com.bookshelfhub.core.common.worker.Worker
import com.bookshelfhub.core.data.repos.bookdownload.IBookDownloadStateRepo
import com.bookshelfhub.core.data.repos.cartitems.ICartItemsRepo
import com.bookshelfhub.core.data.repos.ordered_books.IOrderedBooksRepo
import com.bookshelfhub.core.data.repos.published_books.IPublishedBooksRepo
import com.bookshelfhub.core.data.repos.referral.IReferralRepo
import com.bookshelfhub.core.data.repos.search_history.ISearchHistoryRepo
import com.bookshelfhub.core.data.repos.user.IUserRepo
import com.bookshelfhub.core.data.repos.user_review.IUserReviewRepo
import com.bookshelfhub.core.datastore.settings.SettingsUtil
import com.bookshelfhub.book.page.DownloadBookUseCase
import com.bookshelfhub.core.dynamic_link.IDynamicLink
import com.bookshelfhub.core.dynamic_link.Social
import com.bookshelfhub.core.model.entities.*
import com.bookshelfhub.core.model.uistate.BookDownloadState
import com.bookshelfhub.core.model.uistate.PublishedBookUiState
import com.bookshelfhub.core.remote.remote_config.IRemoteConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.*
import com.bookshelfhub.core.common.helpers.utils.Regex
import com.bookshelfhub.core.common.helpers.utils.datetime.DateTimeUtil
import com.bookshelfhub.core.common.worker.Tag
import com.bookshelfhub.core.data.Book
import com.bookshelfhub.core.domain.usecases.GetBookIdFromCompoundId
import com.bookshelfhub.feature.book.item.workers.AddAFreeBook
import com.bookshelfhub.feature.book.item.workers.GetCollaboratorsCommission
import com.bookshelfhub.feature.book.item.workers.PostUserReview
import javax.inject.Inject

@HiltViewModel
class BookItemActivityViewModel @Inject constructor(
    private val settingsUtil: SettingsUtil,
    savedState: SavedStateHandle,
    private val publishedBooksRepo: IPublishedBooksRepo,
    private  val userReviewRepo: IUserReviewRepo,
    private val orderedBooksRepo: IOrderedBooksRepo,
    private val cartItemsRepo: ICartItemsRepo,
    private val dynamicLink: IDynamicLink,
    private val userRepo: IUserRepo,
    private val worker: Worker,
    private val getBookIdFromCompoundId: GetBookIdFromCompoundId,
    private val downloadBookUseCase: DownloadBookUseCase,
    private val referralRepo: IReferralRepo,
    private val bookDownloadStateRepo: IBookDownloadStateRepo,
    private val searchHistoryRepo: ISearchHistoryRepo,
    private val remoteConfig: IRemoteConfig,
    val userAuth: IUserAuth
): ViewModel(){

  private var userReviews: MutableLiveData<List<UserReview>> = MutableLiveData()
  private var publishedBookOnline: MutableLiveData<PublishedBook> = MutableLiveData()
  private var orderedBook: LiveData<Optional<OrderedBook>> = MutableLiveData()

  private val userId = userAuth.getUserId()
  private val title = savedState.get<String>(Book.NAME)!!
  private val author = savedState.get<String>(Book.AUTHOR)!!
  private val bookId = savedState.get<String>(Book.ID)!!
  private val isSearchItem = savedState.get<Boolean>(Book.IS_SEARCH_ITEM)?:false
  private var bookShareUrl: String? = null
  internal var review:String = ""
  internal var rating:Float = 0F
  private lateinit var user:User

  private val config  = PagingConfig(
    pageSize = 5,
    enablePlaceholders = true,
    initialLoadSize = 10
  )

  init {

    viewModelScope.launch {
      user = userRepo.getUser(userId).get()
    }

    generateBookShareLink()
    orderedBook = orderedBooksRepo.getALiveOptionalOrderedBook(bookId)

    if (isSearchItem){
      val searchHistory = StoreSearchHistory(
        bookId, title, userAuth.getUserId(),
        author, DateTimeUtil.getDateTimeAsString()
      )
      addStoreSearchHistory(searchHistory)
    }

     getARemotePublishedBook()
     getTwoBookReviewsRemotely()
     getRemoteUserReview()
  }

  private fun getRemoteUserReview(){
    viewModelScope.launch {
      val noUserReview = !userReviewRepo.getUserReview(bookId).isPresent
      if(noUserReview){
        try {
            userReviewRepo.getRemoteUserReview(bookId, userId)?.let {
            userReviewRepo.addUserReview(it)
          }
        }catch (e:Exception){
          ErrorUtil.e(e)
          return@launch
        }
      }
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

  suspend fun updatePublishedBook(publishedBook: PublishedBook){
    publishedBooksRepo.updatePublishedBook(publishedBook)
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
      val appCoverUrl = remoteConfig.getString(Social.IMAGE_URL)
      if (bookShareUrl == null){
        val book = publishedBooksRepo.getPublishedBook(bookId).get()
        try {
          val userIdAndEarningsCurrency = "$userId@${user.earningsCurrency}"
          bookShareUrl = dynamicLink.generateShortDynamicLinkAsync(
            book.name,
            book.description,
            appCoverUrl,
            userIdAndEarningsCurrency
          ).toString()
          settingsUtil.setString(bookId, bookShareUrl!!.toString())
        }catch (e:Exception){
          ErrorUtil.e(e)
          return@launch
        }
      }
    }
  }

  fun getBookShareLink(): String? {
    return bookShareUrl
  }

  fun addAFreeOrderedBook(orderedBook: OrderedBook){
    viewModelScope.launch {
      orderedBooksRepo.addAnOrderedBook(orderedBook)
      val workData = workDataOf(
        Book.ID to orderedBook.bookId
      )
      val oneTimeAddAFreeBook = OneTimeWorkRequestBuilder<AddAFreeBook>()
          .setConstraints(Constraint.getConnected())
          .setInputData(workData)
          .build()
      worker.enqueueUniqueWork(Tag.oneTimeAddAFreeBook, ExistingWorkPolicy.REPLACE, oneTimeAddAFreeBook)
    }
  }

   fun getLiveUserReview(): LiveData<Optional<UserReview>> {
     return userReviewRepo.getLiveUserReview(bookId)
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

      val workData = workDataOf(
        Book.ID to cart.bookId,
        Book.PUB_ID to cart.pubId,
        CollabCommission.COLLAB_ID to cart.collaboratorsId
      )
      val oneTimeGetCollabCommissionWorker =
        OneTimeWorkRequestBuilder<GetCollaboratorsCommission>()
          .setConstraints(Constraint.getConnected())
          .setInputData(workData)
          .build()
      worker.enqueueUniqueWork(cart.bookId, ExistingWorkPolicy.KEEP, oneTimeGetCollabCommissionWorker)
    }
  }

   suspend fun getUser(): User {
    return userRepo.getUser(userId).get()
  }

  fun addUserReview(userReview: UserReview, diffInRating:Double){
    viewModelScope.launch{
      userReviewRepo.addUserReview(userReview)
      val urlNotInReview = !userReview.review.containsUrl(Regex.WEB_LINK_IN_TEXT)
      if (urlNotInReview){

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


  private fun getTwoBookReviewsRemotely(){
    viewModelScope.launch {
      try {
        userReviews.value  = userReviewRepo.getRemoteListOfBookReviews(bookId, 3, excludedDocId =  userId)
      }catch (e:Exception){
        ErrorUtil.e(e)
        return@launch
      }
    }
  }

  fun getTwoUserReviewsForBook(): LiveData<List<UserReview>> {
    return userReviews
  }

  private fun getARemotePublishedBook(){
    viewModelScope.launch {
        try {
          publishedBookOnline.value = publishedBooksRepo.getARemotePublishedBook(bookId)!!
        }catch (e:Exception){
          ErrorUtil.e(e)
          return@launch
        }
    }
  }

  fun getBookFromCart(): LiveData<Optional<CartItem>>{
   return  cartItemsRepo.getLiveCartItem(bookId)
  }

  fun getOnlinePublishedBook(): LiveData<PublishedBook> {
    return publishedBookOnline
  }

  fun getSimilarBooksByCategoryPageSource(category:String, bookId: String): Flow<PagingData<PublishedBookUiState>> {
    return Pager(config){
      publishedBooksRepo.getSimilarBooksByCategoryPageSource(category, bookId)
    }.flow
  }


}