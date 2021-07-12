package com.bookshelfhub.bookshelfhub

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.bookshelfhub.bookshelfhub.enums.DbFields
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.PublishedBooks
import com.bookshelfhub.bookshelfhub.services.database.cloud.CloudDb
import com.bookshelfhub.bookshelfhub.services.database.local.LocalDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoreFragmentViewModel @Inject constructor(cloudDb: CloudDb, private val localDb: LocalDb): ViewModel() {

    private val allPubBooks : LiveData<PagingData<PublishedBooks>> = Pager(

        config = PagingConfig(
            pageSize = 10,
            enablePlaceholders = true,
            initialLoadSize = 20
        )

    ){
        localDb.getAllPubBooksPageSource()
    }.liveData

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

    fun getAllBooks(): LiveData<PagingData<PublishedBooks>> {
        return allPubBooks
    }

}