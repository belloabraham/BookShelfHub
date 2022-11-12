package com.bookshelfhub.core.data.repos.read_history

import androidx.lifecycle.LiveData
import com.bookshelfhub.core.model.entities.ReadHistory
import java.util.*

interface IReadHistoryRepo {
    suspend fun addReadHistory(history: ReadHistory)
    fun getLiveReadHistory(id: Int): LiveData<Optional<ReadHistory>>

    suspend fun deleteAllHistory()
}