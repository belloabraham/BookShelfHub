package com.bookshelfhub.core.data.repos.read_history

import com.bookshelfhub.core.database.AppDatabase
import com.bookshelfhub.core.model.entities.ReadHistory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class ReadHistoryRepo @Inject constructor(
    appDatabase: AppDatabase,
   ) : IReadHistoryRepo {
    private val readHistoryDao = appDatabase.getReadHistoryDao()
    private val ioDispatcher: CoroutineDispatcher = IO

     override suspend fun addReadHistory(history: ReadHistory) {
         withContext(ioDispatcher){ readHistoryDao.insertOrReplace(history)}
    }


    override suspend fun getReadHistory(id:Int): Optional<ReadHistory> {
        return readHistoryDao.getReadHistory(id)
    }

     override suspend fun deleteAllHistory() {
         withContext(ioDispatcher){ readHistoryDao.deleteAllHistory()}
    }

}