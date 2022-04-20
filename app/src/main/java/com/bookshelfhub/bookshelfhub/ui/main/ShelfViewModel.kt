package com.bookshelfhub.bookshelfhub.ui.main

import androidx.lifecycle.*
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.IRemoteDataSource
import com.bookshelfhub.bookshelfhub.data.models.entities.OrderedBook
import com.bookshelfhub.bookshelfhub.data.models.entities.ShelfSearchHistory
import com.bookshelfhub.bookshelfhub.data.repos.OrderedBooksRepo
import com.bookshelfhub.bookshelfhub.data.repos.SearchHistoryRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShelfViewModel @Inject constructor(
    private val orderedBooksRepo: OrderedBooksRepo,
    searchHistoryRepo: SearchHistoryRepo,
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