package com.bookshelfhub.core.data.repos.bookinterest

import androidx.lifecycle.LiveData
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import com.bookshelfhub.core.common.worker.Constraint
import com.bookshelfhub.core.common.worker.Tag
import com.bookshelfhub.core.common.worker.Worker
import com.bookshelfhub.core.database.AppDatabase
import com.bookshelfhub.core.model.entities.BookInterest
import com.bookshelfhub.core.remote.database.IRemoteDataSource
import com.bookshelfhub.core.remote.database.RemoteDataFields
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class BookInterestRepo @Inject constructor(
    appDatabase: AppDatabase,
    private val remoteDataSource: IRemoteDataSource,
    private val worker: Worker,
    ) : IBookInterestRepo {

    private val ioDispatcher: CoroutineDispatcher = IO
    private val bookInterestDao = appDatabase.getBookInterestDao()

    override suspend fun getBookInterest(userId:String): Optional<BookInterest> {
        return withContext(ioDispatcher){
            bookInterestDao.getBookInterest(userId)
        }
    }

    override suspend fun updateRemoteUserBookInterest(bookInterest: BookInterest, userId:String): Void? {
       return  remoteDataSource.addDataAsync(
               RemoteDataFields.USERS_COLL,
               userId,
               RemoteDataFields.BOOK_INTEREST,
               bookInterest)
    }

    override fun getLiveBookInterest(userId:String): LiveData<Optional<BookInterest>> {
        return  bookInterestDao.getLiveBookInterest(userId)
    }

    override suspend fun addBookInterest(bookInterest: BookInterest){
        withContext(ioDispatcher) {
            bookInterestDao.insertOrReplace(bookInterest)
        }
        val oneTimeBookInterestDataUpload =
            OneTimeWorkRequestBuilder<UploadBookInterest>()
                .setConstraints(Constraint.getConnected())
                .build()
        worker.enqueueUniqueWork(Tag.oneTimeBookInterestUpload, ExistingWorkPolicy.REPLACE, oneTimeBookInterestDataUpload)
    }


}