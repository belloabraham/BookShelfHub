package com.bookshelfhub.feature.home.ui.shelf

import androidx.lifecycle.*
import androidx.work.Data
import com.bookshelfhub.core.authentication.IUserAuth
import com.bookshelfhub.core.common.helpers.ErrorUtil
import com.bookshelfhub.core.common.helpers.utils.ConnectionUtil
import com.bookshelfhub.core.common.worker.Worker
import com.bookshelfhub.core.data.repos.bookdownload.IBookDownloadStateRepo
import com.bookshelfhub.core.data.repos.ordered_books.IOrderedBooksRepo
import com.bookshelfhub.core.datastore.settings.SettingsUtil
import com.bookshelfhub.book.page.DownloadBookUseCase
import com.bookshelfhub.core.data.Book
import com.bookshelfhub.core.model.entities.OrderedBook
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
    private val connectionUtil: ConnectionUtil,
    private val settingsUtil: SettingsUtil,
    private val worker: Worker,
    private val bookDownloadStateRepo: IBookDownloadStateRepo,
    private val downloadBookUseCase: DownloadBookUseCase,
    val userAuth: IUserAuth
): ViewModel(){
    
    private var liveOrderedBooksUiState: LiveData<List<OrderedBookUiState>> = MutableLiveData()
    private val _isNewlyPurchasedBookFlow = MutableStateFlow(false)
    private var doesUserHaveUnDownloadedPurchasedBooks = _isNewlyPurchasedBookFlow.asStateFlow()
    private val userId = userAuth.getUserId()
    private var liveRemoteListOfOrderedBooks: MutableLiveData<List<OrderedBook>> = MutableLiveData()
    init {
        liveOrderedBooksUiState = orderedBooksRepo.getLiveListOfOrderedBooksUiState(userId)
        getRemoteOrderedBooksForTheFirstTime()
    }


    fun getLocalLiveOrderedBooksAfterRemoteOrderedBooks(): LiveData<List<OrderedBookUiState>> {
        return Transformations.switchMap(getRemoteOrderedBooksForTheFirstTime()) {
            getLiveListOfOrderedBooksUiState()
        }
    }

    fun startBookDownload(workData: Data){
        downloadBookUseCase(worker, workData)
    }


    fun isConnected(): Boolean {
       return connectionUtil.isConnected()
    }

     private fun getLiveListOfOrderedBooksUiState(): LiveData<List<OrderedBookUiState>> {
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

    fun checkIfAnyUnDownloadedPurchasedBook(){
        viewModelScope.launch {
          val isNewlyPurchased =  settingsUtil.getBoolean(Book.IS_NEWLY_PURCHASED, false)
          _isNewlyPurchasedBookFlow.emit(isNewlyPurchased)
        }
    }

    fun updateBookPurchasedBookDownloadStatus(isNewlyPurchased:Boolean){
        viewModelScope.launch {
            _isNewlyPurchasedBookFlow.emit(isNewlyPurchased)
            settingsUtil.setBoolean(Book.IS_NEWLY_PURCHASED, isNewlyPurchased)
        }
    }

     private fun getRemoteOrderedBooksForTheFirstTime(): LiveData<List<OrderedBook>> {
        viewModelScope.launch {
            val totalNoOfOrderedBooks = orderedBooksRepo.getTotalNoOfOrderedBooks()
            val noPreviouslyDownloadedOrderedBooks = totalNoOfOrderedBooks <= 0
            try {
                if(noPreviouslyDownloadedOrderedBooks){
                    val remoteOrderedBooks = orderedBooksRepo.getRemoteOrderedBooks(userId)
                    orderedBooksRepo.addOrderedBooks(remoteOrderedBooks)
                    liveRemoteListOfOrderedBooks.value = remoteOrderedBooks
                }else{
                    liveRemoteListOfOrderedBooks.value = emptyList()
                }
            }catch (e:Exception){
                ErrorUtil.e(e)
                val ifUserHaveOrderedBooks = !(e is FirebaseFirestoreException && e.code == FirebaseFirestoreException.Code.PERMISSION_DENIED)
                if(ifUserHaveOrderedBooks){
                    return@launch
                }
            }
        }
         return liveRemoteListOfOrderedBooks
    }

   suspend fun loadRemoteOrderedBooks(){
       val lastOrderedBookSN = orderedBooksRepo.getTotalNoOfOrderedBooks()
       val remoteOrderedBooks = orderedBooksRepo.getRemoteListOfOrderedBooks(
           userId,
           lastOrderedBookSN.toLong()
       )
       return orderedBooksRepo.addOrderedBooks(remoteOrderedBooks)
    }

}