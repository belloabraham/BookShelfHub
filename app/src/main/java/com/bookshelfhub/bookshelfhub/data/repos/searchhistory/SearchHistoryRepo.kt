package com.bookshelfhub.bookshelfhub.data.repos.searchhistory

import androidx.lifecycle.LiveData
import com.bookshelfhub.bookshelfhub.data.models.entities.ShelfSearchHistory
import com.bookshelfhub.bookshelfhub.data.models.entities.StoreSearchHistory
import com.bookshelfhub.bookshelfhub.data.sources.local.AppDatabase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SearchHistoryRepo @Inject constructor(
    appDatabase: AppDatabase,

    ) : ISearchHistoryRepo {

    private val ioDispatcher: CoroutineDispatcher = IO
    private val searchHistoryDao = appDatabase.getSearchHistoryDao()


    override suspend fun getTop4ShelfSearchHistory(userId:String): List<ShelfSearchHistory> {
        return withContext(ioDispatcher){searchHistoryDao.getTop4ShelfSearchHistory(userId)}
    }

    override suspend fun getTop4StoreSearchHistory(userId:String): List<StoreSearchHistory> {
        return withContext(ioDispatcher){searchHistoryDao.getTop4StoreSearchHistory(userId)}
    }

     override suspend fun addStoreSearchHistory(searchHistory: StoreSearchHistory){
         withContext(ioDispatcher){searchHistoryDao.addStoreSearchHistory(searchHistory)}
    }

     override suspend fun addShelfSearchHistory(shelfSearchHistory: ShelfSearchHistory){
        withContext(ioDispatcher){searchHistoryDao.addShelfSearchHistory(shelfSearchHistory)}
    }
}