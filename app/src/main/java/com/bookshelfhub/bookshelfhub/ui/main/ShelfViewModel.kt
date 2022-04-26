package com.bookshelfhub.bookshelfhub.ui.main

import androidx.lifecycle.*
import com.bookshelfhub.bookshelfhub.data.Book
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.data.models.entities.OrderedBook
import com.bookshelfhub.bookshelfhub.data.models.entities.ShelfSearchHistory
import com.bookshelfhub.bookshelfhub.data.models.uistate.BookDownloadState
import com.bookshelfhub.bookshelfhub.data.repos.bookdownload.IBookDownloadStateRepo
import com.bookshelfhub.bookshelfhub.data.repos.orderedbooks.IOrderedBooksRepo
import com.bookshelfhub.bookshelfhub.data.repos.searchhistory.ISearchHistoryRepo
import com.bookshelfhub.bookshelfhub.domain.usecases.GetBookIdFromPossibleMergeIdsUseCase
import com.bookshelfhub.bookshelfhub.helpers.utils.ConnectionUtil
import com.bookshelfhub.bookshelfhub.helpers.settings.SettingsUtil
import com.google.common.base.Optional
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ShelfViewModel @Inject constructor(
    private val orderedBooksRepo: IOrderedBooksRepo,
    searchHistoryRepo: ISearchHistoryRepo,
    private val connectionUtil: ConnectionUtil,
    private val settingsUtil: SettingsUtil,
    private val bookDownloadStateRepo: IBookDownloadStateRepo,
    private val getBookIdFromPossibleMergeIdsUseCase: GetBookIdFromPossibleMergeIdsUseCase,
    val userAuth:IUserAuth): ViewModel(){
    
    private var liveOrderedBooks: LiveData<List<OrderedBook>> = MutableLiveData()
    private var shelfShelfSearchHistory: LiveData<List<ShelfSearchHistory>> = MutableLiveData()
    private val _isNewlyPurchasedBookFlow = MutableStateFlow(false)
    private var doesUserHaveUnDownloadedPurchasedBooks = _isNewlyPurchasedBookFlow.asStateFlow()
    private val userId:String = userAuth.getUserId()


    init {
        shelfShelfSearchHistory = searchHistoryRepo.getLiveShelfSearchHistory(userId)
        liveOrderedBooks = orderedBooksRepo.getLiveOrderedBooks(userId)
    }

    fun getBookIdFromPossiblyMergedIds(possiblyMergedIds:String): String {
        return getBookIdFromPossibleMergeIdsUseCase(possiblyMergedIds)
    }

    fun isConnected(): Boolean {
       return connectionUtil.isConnected()
    }

    fun addDownloadState(bookDownloadState:BookDownloadState){
        viewModelScope.launch {
            bookDownloadStateRepo.addDownloadState(bookDownloadState)
        }
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
                if(connectionUtil.isConnected()){
                val localOrderedBooks = orderedBooksRepo.getOrderedBooks(userId)
                val lastOrderedBookSN =  if(localOrderedBooks.isEmpty()) 0 else localOrderedBooks.size
                try {
                   val remoteOrderedBooks = orderedBooksRepo.getRemoteListOfOrderedBooks(
                       userId,
                       lastOrderedBookSN.toLong()
                   )
                   orderedBooksRepo.addOrderedBooks(remoteOrderedBooks)
                }catch (e:Exception){
                    Timber.e(e)
                    return@launch
                }
            }
        }
    }


    fun getShelfSearchHistory():LiveData<List<ShelfSearchHistory>>{
        return shelfShelfSearchHistory
    }

    fun getLiveOrderedBooks():LiveData<List<OrderedBook>>{
        return liveOrderedBooks
    }

}