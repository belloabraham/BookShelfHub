package com.bookshelfhub.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bookshelfhub.core.model.entities.StoreSearchHistory

@Dao
interface SearchHistoryDao{

    @Query("SELECT * FROM StoreSearchHistories WHERE userId = :userId Order BY dateTime DESC LIMIT 8")
    suspend fun getTop4StoreSearchHistory(userId:String): List<StoreSearchHistory>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addStoreSearchHistory(searchHistory: StoreSearchHistory)

}