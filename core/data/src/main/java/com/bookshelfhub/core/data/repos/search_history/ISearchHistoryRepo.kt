package com.bookshelfhub.core.data.repos.search_history

import com.bookshelfhub.core.model.entities.ShelfSearchHistory
import com.bookshelfhub.core.model.entities.StoreSearchHistory

interface ISearchHistoryRepo {
    suspend fun getTop4StoreSearchHistory(userId:String): List<StoreSearchHistory>
    suspend fun getTop4ShelfSearchHistory(userId: String): List<ShelfSearchHistory>
    suspend fun addStoreSearchHistory(searchHistory: StoreSearchHistory)
    suspend fun addShelfSearchHistory(shelfSearchHistory: ShelfSearchHistory)
}