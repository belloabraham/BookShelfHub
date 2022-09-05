package com.bookshelfhub.bookshelfhub.data.sources.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bookshelfhub.bookshelfhub.data.models.entities.ShelfSearchHistory
import com.bookshelfhub.bookshelfhub.data.models.entities.StoreSearchHistory

@Dao
interface SearchHistoryDao{

    @Query("SELECT * FROM ShelfSearchHistories WHERE userId = :userId Order BY dateTime DESC LIMIT 8")
    suspend fun getTop4ShelfSearchHistory(userId:String):List<ShelfSearchHistory>

    @Query("SELECT * FROM StoreSearchHistories WHERE userId = :userId Order BY dateTime DESC LIMIT 8")
    fun getTop4StoreSearchHistory(userId:String): List<StoreSearchHistory>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addStoreSearchHistory(searchHistory: StoreSearchHistory)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addShelfSearchHistory(shelfSearchHistory: ShelfSearchHistory)

}