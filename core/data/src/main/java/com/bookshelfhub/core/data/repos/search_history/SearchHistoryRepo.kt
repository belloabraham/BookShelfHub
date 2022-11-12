package com.bookshelfhub.core.data.repos.search_history

import com.bookshelfhub.core.database.AppDatabase
import com.bookshelfhub.core.model.entities.ShelfSearchHistory
import com.bookshelfhub.core.model.entities.StoreSearchHistory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SearchHistoryRepo @Inject constructor(
    appDatabase: AppDatabase
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