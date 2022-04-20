package com.bookshelfhub.bookshelfhub.data.repos.sources.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.bookshelfhub.bookshelfhub.data.models.entities.ReadHistory
import com.google.common.base.Optional

@Dao
abstract class ReadHistoryDao : BaseDao<ReadHistory> {

    @Query("SELECT * FROM ReadHistories WHERE id =:id")
    abstract fun getLiveReadHistory(id:Int):LiveData<Optional<ReadHistory>>

    @Query("DELETE FROM ReadHistories")
    abstract suspend fun deleteAllHistory()
}