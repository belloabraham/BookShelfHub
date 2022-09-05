package com.bookshelfhub.bookshelfhub.data.repos.searchhistory

import androidx.lifecycle.LiveData
import com.bookshelfhub.bookshelfhub.data.models.entities.ShelfSearchHistory
import com.bookshelfhub.bookshelfhub.data.models.entities.StoreSearchHistory

interface ISearchHistoryRepo {
    suspend fun getTop4StoreSearchHistory(userId:String): List<StoreSearchHistory>
    suspend fun getTop4ShelfSearchHistory(userId: String): List<ShelfSearchHistory>
    suspend fun addStoreSearchHistory(searchHistory: StoreSearchHistory)
    suspend fun addShelfSearchHistory(shelfSearchHistory: ShelfSearchHistory)
}