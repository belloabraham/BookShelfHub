package com.bookshelfhub.bookshelfhub.data.repos

import androidx.lifecycle.LiveData
import com.bookshelfhub.bookshelfhub.data.models.entities.History
import com.bookshelfhub.bookshelfhub.data.models.entities.PaymentTransaction
import com.bookshelfhub.bookshelfhub.data.models.entities.UserReview
import com.bookshelfhub.bookshelfhub.data.repos.sources.local.ReadHistoryDao
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.IRemoteDataSource
import com.google.common.base.Optional
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ReadHistoryRepo @Inject constructor(private val readHistoryDao: ReadHistoryDao) {
    
     suspend fun addReadHistory(history: History) {
         withContext(IO){ readHistoryDao.addReadHistory(history)}
    }

     fun getLiveReadHistory(id:Int): LiveData<Optional<History>> {
        return readHistoryDao.getLiveReadHistory(id)
    }

     suspend fun deleteAllHistory() {
         withContext(IO){ readHistoryDao.deleteAllHistory()}
    }

}