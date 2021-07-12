package com.bookshelfhub.bookshelfhub

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bookshelfhub.bookshelfhub.enums.DbFields
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.PublishedBooks
import com.bookshelfhub.bookshelfhub.services.database.cloud.CloudDb
import com.bookshelfhub.bookshelfhub.services.database.local.LocalDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoreFragmentViewModel @Inject constructor(private val cloudDb: CloudDb, private val localDb: LocalDb): ViewModel() {
    private var user: LiveData<User> = MutableLiveData()

    init {
        cloudDb.getLiveListOfDataAsync(
            DbFields.PUBLISHED_BOOKS.KEY,
            DbFields.PUBLISHED_BOOK.KEY,
            PublishedBooks::class.java
        ){
            if (it.isNotEmpty()){
                viewModelScope.launch(IO){
                    localDb.addAllPubBooks(it)
                }
            }
        }
    }

}