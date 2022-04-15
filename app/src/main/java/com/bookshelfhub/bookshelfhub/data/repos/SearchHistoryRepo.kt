package com.bookshelfhub.bookshelfhub.data.repos

import androidx.lifecycle.LiveData
import com.bookshelfhub.bookshelfhub.data.models.entities.PubReferrers
import com.bookshelfhub.bookshelfhub.data.models.entities.ShelfSearchHistory
import com.bookshelfhub.bookshelfhub.data.models.entities.StoreSearchHistory
import com.bookshelfhub.bookshelfhub.data.repos.sources.local.SearchHistoryDao
import com.google.common.base.Optional
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SearchHistoryRepo @Inject constructor(private val searchHistoryDao: SearchHistoryDao) {
    
    fun getLiveStoreSearchHistory(userId:String): LiveData<List<StoreSearchHistory>> {
        return searchHistoryDao.getLiveStoreSearchHistory(userId)
    }

     suspend fun addStoreSearchHistory(searchHistory: StoreSearchHistory){
         withContext(IO){searchHistoryDao.addStoreSearchHistory(searchHistory)}
    }

     suspend fun addShelfSearchHistory(shelfSearchHistory: ShelfSearchHistory){
        withContext(IO){searchHistoryDao.addShelfSearchHistory(shelfSearchHistory)}
    }

     fun getLiveShelfSearchHistory(userId:String):LiveData<List<ShelfSearchHistory>> {
        return  searchHistoryDao.getLiveShelfSearchHistory(userId)
    }

}