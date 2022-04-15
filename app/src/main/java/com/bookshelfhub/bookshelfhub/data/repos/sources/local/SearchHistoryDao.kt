package com.bookshelfhub.bookshelfhub.data.repos.sources.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bookshelfhub.bookshelfhub.data.models.entities.BookInterest
import com.bookshelfhub.bookshelfhub.data.models.entities.ShelfSearchHistory
import com.bookshelfhub.bookshelfhub.data.models.entities.StoreSearchHistory
import com.google.common.base.Optional

@Dao
interface SearchHistoryDao {
    @Query("SELECT * FROM ShelfSearchHistory WHERE userId = :userId Order BY dateTime DESC LIMIT 4")
    fun getLiveShelfSearchHistory(userId:String): LiveData<List<ShelfSearchHistory>>

    @Query("SELECT * FROM StoreSearchHistory WHERE userId = :userId Order BY dateTime DESC LIMIT 4")
    fun getLiveStoreSearchHistory(userId:String): LiveData<List<StoreSearchHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addStoreSearchHistory(searchHistory: StoreSearchHistory)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addShelfSearchHistory(shelfSearchHistory: ShelfSearchHistory)
}