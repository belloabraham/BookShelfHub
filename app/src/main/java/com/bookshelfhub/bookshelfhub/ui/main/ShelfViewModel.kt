package com.bookshelfhub.bookshelfhub.ui.main

import androidx.lifecycle.*
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.data.models.entities.OrderedBook
import com.bookshelfhub.bookshelfhub.data.models.entities.ShelfSearchHistory
import com.bookshelfhub.bookshelfhub.data.repos.orderedbooks.IOrderedBooksRepo
import com.bookshelfhub.bookshelfhub.data.repos.searchhistory.ISearchHistoryRepo
import com.bookshelfhub.bookshelfhub.data.sources.remote.IRemoteDataSource
import com.bookshelfhub.bookshelfhub.helpers.utils.ConnectionUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ShelfViewModel @Inject constructor(
    private val orderedBooksRepo: IOrderedBooksRepo,
    searchHistoryRepo: ISearchHistoryRepo,
    private val connectionUtil: ConnectionUtil,
    val userAuth:IUserAuth): ViewModel(){
    
    private var liveOrderedBooks: LiveData<List<OrderedBook>> = MutableLiveData()
    private var shelfShelfSearchHistory: LiveData<List<ShelfSearchHistory>> = MutableLiveData()

    private val userId:String = userAuth.getUserId()


    init {
        shelfShelfSearchHistory = searchHistoryRepo.getLiveShelfSearchHistory(userId)
        liveOrderedBooks = orderedBooksRepo.getLiveOrderedBooks(userId)
    }

    suspend fun getOrderedBooks(): List<OrderedBook> {
        return orderedBooksRepo.getOrderedBooks(userId)
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

    fun addOrderedBooks(orderedBooks: List<OrderedBook>){
        viewModelScope.launch {
            orderedBooksRepo.addOrderedBooks(orderedBooks)
        }
    }

    fun getShelfSearchHistory():LiveData<List<ShelfSearchHistory>>{
        return shelfShelfSearchHistory
    }

    fun getLiveOrderedBooks():LiveData<List<OrderedBook>>{
        return liveOrderedBooks
    }

}