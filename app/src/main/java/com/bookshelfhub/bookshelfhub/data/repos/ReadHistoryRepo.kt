package com.bookshelfhub.bookshelfhub.data.repos

import androidx.lifecycle.LiveData
import com.bookshelfhub.bookshelfhub.data.models.entities.ReadHistory
import com.bookshelfhub.bookshelfhub.data.repos.sources.local.ReadHistoryDao
import com.google.common.base.Optional
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ReadHistoryRepo @Inject constructor(private val readHistoryDao: ReadHistoryDao) {
    
     suspend fun addReadHistory(history: ReadHistory) {
         withContext(IO){ readHistoryDao.insertOrReplace(history)}
    }

     fun getLiveReadHistory(id:Int): LiveData<Optional<ReadHistory>> {
        return readHistoryDao.getLiveReadHistory(id)
    }

     suspend fun deleteAllHistory() {
         withContext(IO){ readHistoryDao.deleteAllHistory()}
    }

}