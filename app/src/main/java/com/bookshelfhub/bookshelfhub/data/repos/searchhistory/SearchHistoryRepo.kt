package com.bookshelfhub.bookshelfhub.data.repos.searchhistory

import androidx.lifecycle.LiveData
import com.bookshelfhub.bookshelfhub.data.models.entities.ShelfSearchHistory
import com.bookshelfhub.bookshelfhub.data.models.entities.StoreSearchHistory
import com.bookshelfhub.bookshelfhub.data.sources.local.SearchHistoryDao
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SearchHistoryRepo @Inject constructor(private val searchHistoryDao: SearchHistoryDao) :
    ISearchHistoryRepo {
    
    override fun getLiveStoreSearchHistory(userId:String): LiveData<List<StoreSearchHistory>> {
        return searchHistoryDao.getLiveStoreSearchHistory(userId)
    }

     override suspend fun addStoreSearchHistory(searchHistory: StoreSearchHistory){
         withContext(IO){searchHistoryDao.addStoreSearchHistory(searchHistory)}
    }

     override suspend fun addShelfSearchHistory(shelfSearchHistory: ShelfSearchHistory){
        withContext(IO){searchHistoryDao.addShelfSearchHistory(shelfSearchHistory)}
    }

     override fun getLiveShelfSearchHistory(userId:String):LiveData<List<ShelfSearchHistory>> {
        return  searchHistoryDao.getLiveShelfSearchHistory(userId)
    }

}