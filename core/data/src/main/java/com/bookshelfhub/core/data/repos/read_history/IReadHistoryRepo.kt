package com.bookshelfhub.core.data.repos.read_history

import com.bookshelfhub.core.model.entities.ReadHistory
import java.util.*

interface IReadHistoryRepo {
    suspend fun addReadHistory(history: ReadHistory)
    suspend fun getReadHistory(bookId:String): Optional<ReadHistory>
    suspend fun deleteAllHistory()
}