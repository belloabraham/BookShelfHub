package com.bookshelfhub.feature.home.ui.shelf

import androidx.lifecycle.*
import androidx.work.Data
import com.bookshelfhub.core.authentication.IUserAuth
import com.bookshelfhub.core.common.helpers.ErrorUtil
import com.bookshelfhub.core.common.helpers.utils.ConnectionUtil
import com.bookshelfhub.core.common.worker.Worker
import com.bookshelfhub.core.data.repos.bookdownload.IBookDownloadStateRepo
import com.bookshelfhub.core.data.repos.ordered_books.IOrderedBooksRepo
import com.bookshelfhub.core.data.repos.search_history.ISearchHistoryRepo
import com.bookshelfhub.core.datastore.settings.SettingsUtil
import com.bookshelfhub.core.domain.usecases.DownloadBookUseCase
import com.bookshelfhub.core.domain.usecases.GetBookIdFromCompoundId
import com.bookshelfhub.core.model.entities.ShelfSearchHistory
import com.bookshelfhub.core.model.uistate.BookDownloadState
import com.bookshelfhub.core.model.uistate.OrderedBookUiState
import com.google.firebase.firestore.FirebaseFirestoreException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ShelfViewModel @Inject constructor(
    private val orderedBooksRepo: IOrderedBooksRepo,
    private val searchHistoryRepo: ISearchHistoryRepo,
    private val connectionUtil: ConnectionUtil,
    private val settingsUtil: SettingsUtil,
    private val worker: Worker,
    private val bookDownloadStateRepo: IBookDownloadStateRepo,
    private val getBookIdFromCompoundId: GetBookIdFromCompoundId,
    private val downloadBookUseCase: DownloadBookUseCase,
    val userAuth: IUserAuth
): ViewModel(){
    
    private var liveOrderedBooksUiState: LiveData<List<OrderedBookUiState>> = MutableLiveData()
    private val _isNewlyPurchasedBookFlow = MutableStateFlow(false)
    private var doesUserHaveUnDownloadedPurchasedBooks = _isNewlyPurchasedBookFlow.asStateFlow()
    private val userId:String = userAuth.getUserId()

    init {
        liveOrderedBooksUiState = orderedBooksRepo.getLiveListOfOrderedBooksUiState(userId)
    }


    fun startBookDownload(workData: Data){
        viewModelScope.launch {
            downloadBookUseCase(worker, workData, bookDownloadStateRepo)
        }
    }

    fun getBookIdFromPossiblyMergedIds(possiblyMergedIds:String): String {
        return getBookIdFromCompoundId(possiblyMergedIds)
    }

    fun isConnected(): Boolean {
       return connectionUtil.isConnected()
    }

     fun getLiveListOfOrderedBooksUiState(): LiveData<List<OrderedBookUiState>> {
      return  liveOrderedBooksUiState
    }

    fun deleteDownloadState(bookDownloadState: BookDownloadState){
        viewModelScope.launch {
            bookDownloadStateRepo.deleteDownloadState(bookDownloadState)
        }
    }

    fun getLiveBookDownloadState(bookId:String): LiveData<Optional<BookDownloadState>> {
       return bookDownloadStateRepo.getLiveBookDownloadState(bookId)
    }

    fun doesUserHaveUnDownloadedPurchasedBooks(): StateFlow<Boolean> {
        return doesUserHaveUnDownloadedPurchasedBooks
    }

    fun checkIfUserHaveUnDownloadedPurchasedBook(){
        viewModelScope.launch {
          val isNewlyPurchased =  settingsUtil.getBoolean(com.bookshelfhub.core.data.Book.IS_NEWLY_PURCHASED, false)
          _isNewlyPurchasedBookFlow.emit(isNewlyPurchased)
        }
    }

    fun updateBookPurchaseState(isNewlyPurchased:Boolean){
        viewModelScope.launch {
            _isNewlyPurchasedBookFlow.emit(isNewlyPurchased)
            settingsUtil.setBoolean(com.bookshelfhub.core.data.Book.IS_NEWLY_PURCHASED, isNewlyPurchased)
        }
    }

     fun getRemoteOrderedBooksRepeatedly(){
         viewModelScope.launch {
             try {
                 val lastOrderedBookSN = orderedBooksRepo.getTotalNoOfOrderedBooks()
                 val remoteOrderedBooks = orderedBooksRepo.getRemoteListOfOrderedBooks(
                     userId,
                     lastOrderedBookSN.toLong()
                 )
                 orderedBooksRepo.addOrderedBooks(remoteOrderedBooks)
             }catch (e:Exception){
                 ErrorUtil.e(e)
                 val isFirebasePermissionDeniedError = (e is FirebaseFirestoreException && e.code == FirebaseFirestoreException.Code.PERMISSION_DENIED)
                 //User have no ordered books in record, likely a new user
                 val isNotFirebasePermissionDeniedError  = !isFirebasePermissionDeniedError
                 if(isNotFirebasePermissionDeniedError){
                     ErrorUtil.e(e)
                     return@launch
                 }
             }

         }

    }

   suspend fun loadRemoteOrderedBooks(){
       val lastOrderedBookSN = orderedBooksRepo.getTotalNoOfOrderedBooks()
       val remoteOrderedBooks = orderedBooksRepo.getRemoteListOfOrderedBooks(
           userId,
           lastOrderedBookSN.toLong()
       )
       return orderedBooksRepo.addOrderedBooks(remoteOrderedBooks)
    }

    suspend fun getTop4ShelfSearchHistory():List<ShelfSearchHistory>{
        return searchHistoryRepo.getTop4ShelfSearchHistory(userId)
    }

}