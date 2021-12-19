package com.bookshelfhub.bookshelfhub.ui.main

import androidx.lifecycle.*
import com.bookshelfhub.bookshelfhub.services.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.services.database.cloud.ICloudDb
import com.bookshelfhub.bookshelfhub.services.database.local.ILocalDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.OrderedBooks
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.ShelfSearchHistory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShelfViewModel @Inject constructor(
    val cloudDb: ICloudDb, val userAuth:IUserAuth, val localDb: ILocalDb, ): ViewModel(){
    private var liveOrderedBooks: LiveData<List<OrderedBooks>> = MutableLiveData()
    private var shelfShelfSearchHistory: LiveData<List<ShelfSearchHistory>> = MutableLiveData()
    private lateinit var orderedBooks:List<OrderedBooks>

    private val userId:String = userAuth.getUserId()


    init {
        shelfShelfSearchHistory = localDb.getLiveShelfSearchHistory(userId)
        liveOrderedBooks = localDb.getLiveOrderedBooks(userId)
        viewModelScope.launch(IO){
            orderedBooks = localDb.getOrderedBooks(userId)
        }
    }

    fun getOrderedBooks(): List<OrderedBooks> {
        return orderedBooks
    }

    fun addOrderedBooks(orderedBooks: List<OrderedBooks>){
        viewModelScope.launch(IO) {
            localDb.addOrderedBooks(orderedBooks)
        }
    }

    fun getShelfSearchHistory():LiveData<List<ShelfSearchHistory>>{
        return shelfShelfSearchHistory
    }

    fun getLiveOrderedBooks():LiveData<List<OrderedBooks>>{
        return liveOrderedBooks
    }

}