package com.bookshelfhub.bookshelfhub.data.repos.bookinterest

import androidx.lifecycle.LiveData
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import com.bookshelfhub.bookshelfhub.data.models.entities.BookInterest
import com.bookshelfhub.bookshelfhub.data.sources.local.BookInterestDao
import com.bookshelfhub.bookshelfhub.data.sources.local.RoomInstance
import com.bookshelfhub.bookshelfhub.data.sources.remote.IRemoteDataSource
import com.bookshelfhub.bookshelfhub.data.sources.remote.RemoteDataFields
import com.bookshelfhub.bookshelfhub.workers.*
import com.google.common.base.Optional
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BookInterestRepo @Inject constructor(
    private val roomInstance: RoomInstance,
    private val remoteDataSource: IRemoteDataSource,
    private val worker:Worker,
    ) : IBookInterestRepo {

    private val ioDispatcher: CoroutineDispatcher = IO
    private val bookInterestDao = roomInstance.getBookInterestDao()

    override suspend fun getBookInterest(userId:String): Optional<BookInterest> {
        return withContext(ioDispatcher){
            bookInterestDao.getBookInterest(userId)
        }
    }

    override suspend fun updateRemoteUserBookInterest(bookInterest: BookInterest, userId:String): Void? {
       return  withContext(ioDispatcher){
           remoteDataSource.addDataAsync(
               RemoteDataFields.USERS_COLL,
               userId,
               RemoteDataFields.BOOK_INTEREST,
               bookInterest)
       }
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