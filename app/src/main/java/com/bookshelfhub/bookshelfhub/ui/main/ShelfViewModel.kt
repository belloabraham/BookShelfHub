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
    private var orderedBooks: LiveData<List<OrderedBooks>> = MutableLiveData()
    private var shelfShelfSearchHistory: LiveData<List<ShelfSearchHistory>> = MutableLiveData()

    private val userId:String = userAuth.getUserId()


    init {
        shelfShelfSearchHistory = localDb.getLiveShelfSearchHistory(userId)
        orderedBooks = localDb.getLiveOrderedBooks(userId)
    }


    fun addOrderedBooks(orderedBooks: List<OrderedBooks>){
        viewModelScope.launch(IO) {
            localDb.addOrderedBooks(orderedBooks)
        }
    }

    fun getShelfSearchHistory():LiveData<List<ShelfSearchHistory>>{
        return shelfShelfSearchHistory
    }

    fun getOrderedBooks():LiveData<List<OrderedBooks>>{
        return orderedBooks
    }

}