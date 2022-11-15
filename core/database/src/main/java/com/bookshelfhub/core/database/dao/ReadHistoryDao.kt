package com.bookshelfhub.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.bookshelfhub.core.model.entities.ReadHistory
import java.util.*
@Dao
abstract class ReadHistoryDao : BaseDao<ReadHistory> {

    @Query("SELECT * FROM ReadHistories WHERE id =:id")
    abstract fun getReadHistory(id:Int): Optional<ReadHistory>

    @Query("DELETE FROM ReadHistories")
    abstract suspend fun deleteAllHistory()
}