package com.bookshelfhub.bookshelfhub.data.repos.readhistory

import androidx.lifecycle.LiveData
import com.bookshelfhub.bookshelfhub.data.models.entities.ReadHistory
import com.bookshelfhub.bookshelfhub.data.sources.local.ReadHistoryDao
import com.google.common.base.Optional
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ReadHistoryRepo @Inject constructor(
    private val readHistoryDao: ReadHistoryDao,
    private val ioDispatcher: CoroutineDispatcher = IO) :
    IReadHistoryRepo {
    
     override suspend fun addReadHistory(history: ReadHistory) {
         withContext(ioDispatcher){ readHistoryDao.insertOrReplace(history)}
    }

     override fun getLiveReadHistory(id:Int): LiveData<Optional<ReadHistory>> {
        return readHistoryDao.getLiveReadHistory(id)
    }

     override suspend fun deleteAllHistory() {
         withContext(ioDispatcher){ readHistoryDao.deleteAllHistory()}
    }

}