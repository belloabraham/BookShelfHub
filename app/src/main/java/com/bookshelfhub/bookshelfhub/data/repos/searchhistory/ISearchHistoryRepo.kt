package com.bookshelfhub.bookshelfhub.data.repos.searchhistory

import androidx.lifecycle.LiveData
import com.bookshelfhub.bookshelfhub.data.models.entities.ShelfSearchHistory
import com.bookshelfhub.bookshelfhub.data.models.entities.StoreSearchHistory

interface ISearchHistoryRepo {
    fun getLiveStoreSearchHistory(userId: String): LiveData<List<StoreSearchHistory>>

    suspend fun addStoreSearchHistory(searchHistory: StoreSearchHistory)
    suspend fun addShelfSearchHistory(shelfSearchHistory: ShelfSearchHistory)
    fun getLiveShelfSearchHistory(userId: String): LiveData<List<ShelfSearchHistory>>
}