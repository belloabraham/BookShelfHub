package com.bookshelfhub.bookshelfhub.data.repos.sources.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bookshelfhub.bookshelfhub.data.models.entities.BookInterest
import com.bookshelfhub.bookshelfhub.data.models.entities.History
import com.google.common.base.Optional

@Dao
interface ReadHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addReadHistory(history: History)

    @Query("SELECT * FROM History WHERE id =:id")
    fun getLiveReadHistory(id:Int):LiveData<Optional<History>>

    @Query("DELETE FROM History")
    suspend fun deleteAllHistory()
}