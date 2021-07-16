package com.bookshelfhub.bookshelfhub

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.bookshelfhub.bookshelfhub.services.database.local.LocalDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.PublishedBooks
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CategoryActivityViewModel @Inject constructor(private val localDb: LocalDb) : ViewModel(){

    private var bookByCategory: LiveData<PagingData<PublishedBooks>> = MutableLiveData()
    private var liveBooksByCategory: LiveData<List<PublishedBooks>> = MutableLiveData()

    private val config  = PagingConfig(
        pageSize = 10,
        enablePlaceholders = true,
        initialLoadSize = 20
    )

    fun loadLiveBooksByCategory(category: String){
       liveBooksByCategory = localDb.getLiveBooksByCategory(category)
    }
    fun loadBooksByCategory(category:String){
        bookByCategory = Pager(config){
            localDb.getBooksByCategoryPageSource(category)
        }.liveData
    }

    fun getBookByCategory(): LiveData<PagingData<PublishedBooks>> {
        return bookByCategory
    }

    fun getLiveBooksByCategory(): LiveData<List<PublishedBooks>> {
        return liveBooksByCategory
    }

}
