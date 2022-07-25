package com.bookshelfhub.bookshelfhub.ui.main.shelf

import androidx.lifecycle.*
import androidx.work.Data
import com.bookshelfhub.bookshelfhub.data.Book
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.data.models.entities.OrderedBook
import com.bookshelfhub.bookshelfhub.data.models.entities.ShelfSearchHistory
import com.bookshelfhub.bookshelfhub.data.models.uistate.BookDownloadState
import com.bookshelfhub.bookshelfhub.data.models.uistate.OrderedBookUiState
import com.bookshelfhub.bookshelfhub.data.repos.bookdownload.IBookDownloadStateRepo
import com.bookshelfhub.bookshelfhub.data.repos.orderedbooks.IOrderedBooksRepo
import com.bookshelfhub.bookshelfhub.data.repos.searchhistory.ISearchHistoryRepo
import com.bookshelfhub.bookshelfhub.domain.usecases.DownloadBookUseCase
import com.bookshelfhub.bookshelfhub.domain.usecases.GetBookIdFromCompoundId
import com.bookshelfhub.bookshelfhub.helpers.utils.ConnectionUtil
import com.bookshelfhub.bookshelfhub.helpers.settings.SettingsUtil
import com.bookshelfhub.bookshelfhub.workers.Worker
import com.google.common.base.Optional
import com.google.firebase.firestore.FirebaseFirestoreException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class ShelfViewModel @Inject constructor(
    private val orderedBooksRepo: IOrderedBooksRepo,
    searchHistoryRepo: ISearchHistoryRepo,
    private val connectionUtil: ConnectionUtil,
    private val settingsUtil: SettingsUtil,
    private val worker: Worker,
    private val bookDownloadStateRepo: IBookDownloadStateRepo,
    private val getBookIdFromCompoundId: GetBookIdFromCompoundId,
    private val downloadBookUseCase: DownloadBookUseCase,
    val userAuth:IUserAuth): ViewModel(){
    
    private var liveOrderedBooksUiState: LiveData<List<OrderedBookUiState>> = MutableLiveData()
    private var shelfShelfSearchHistory: LiveData<List<ShelfSearchHistory>> = MutableLiveData()
    private val _isNewlyPurchasedBookFlow = MutableStateFlow(false)
    private var doesUserHaveUnDownloadedPurchasedBooks = _isNewlyPurchasedBookFlow.asStateFlow()
    private val userId:String = userAuth.getUserId()

    init {
        shelfShelfSearchHistory = searchHistoryRepo.getLiveShelfSearchHistory(userId)
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
          val isNewlyPurchased =  settingsUtil.getBoolean(Book.IS_NEWLY_PURCHASED, false)
            _isNewlyPurchasedBookFlow.emit(isNewlyPurchased)
        }
    }

    fun updateBookPurchaseState(isNewlyPurchased:Boolean){
        viewModelScope.launch {
            _isNewlyPurchasedBookFlow.emit(isNewlyPurchased)
            settingsUtil.setBoolean(Book.IS_NEWLY_PURCHASED, isNewlyPurchased)
        }
    }

    fun getRemoteOrderedBooks(){
            viewModelScope.launch {
                val lastOrderedBookSN = orderedBooksRepo.getTotalNoOfOrderedBooks()
                try {
                   val remoteOrderedBooks = orderedBooksRepo.getRemoteListOfOrderedBooks(
                       userId,
                       lastOrderedBookSN.toLong()
                   )
                   orderedBooksRepo.addOrderedBooks(remoteOrderedBooks)
                }catch (e:Exception){
                    val userHaveAtLeastOneBooksInDatabaseShelf = (e is IOException) ||  !(e is FirebaseFirestoreException && e.code == FirebaseFirestoreException.Code.PERMISSION_DENIED)
                    if(userHaveAtLeastOneBooksInDatabaseShelf){
                        Timber.e(e)
                        return@launch
                    }
                }
        }
    }

    fun getShelfSearchHistory():LiveData<List<ShelfSearchHistory>>{
        return shelfShelfSearchHistory
    }

}