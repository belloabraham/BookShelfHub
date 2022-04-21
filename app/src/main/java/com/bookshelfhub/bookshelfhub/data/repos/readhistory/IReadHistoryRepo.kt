package com.bookshelfhub.bookshelfhub.data.repos.readhistory

import androidx.lifecycle.LiveData
import com.bookshelfhub.bookshelfhub.data.models.entities.ReadHistory
import com.google.common.base.Optional

interface IReadHistoryRepo {
    suspend fun addReadHistory(history: ReadHistory)
    fun getLiveReadHistory(id: Int): LiveData<Optional<ReadHistory>>

    suspend fun deleteAllHistory()
}