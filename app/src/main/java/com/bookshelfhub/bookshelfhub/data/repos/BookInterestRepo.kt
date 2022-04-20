package com.bookshelfhub.bookshelfhub.data.repos

import androidx.lifecycle.LiveData
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import com.bookshelfhub.bookshelfhub.data.models.entities.BookInterest
import com.bookshelfhub.bookshelfhub.data.repos.sources.local.BookInterestDao
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.IRemoteDataSource
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.RemoteDataFields
import com.bookshelfhub.bookshelfhub.workers.*
import com.google.common.base.Optional
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BookInterestRepo @Inject constructor(
    private val bookInterestDao: BookInterestDao,
    private val remoteDataSource: IRemoteDataSource,
    private val worker:Worker) {

    suspend fun getBookInterest(userId:String): Optional<BookInterest> {
        return   withContext(IO){
            bookInterestDao.getBookInterest(userId)
        }
    }

    suspend fun updateRemoteUserBookInterest(bookInterest: BookInterest, userId:String): Void? {
       return remoteDataSource.addDataAsync(RemoteDataFields.USERS_COLL, userId, RemoteDataFields.BOOK_INTEREST, bookInterest)
    }

    fun getLiveBookInterest(userId:String): LiveData<Optional<BookInterest>> {
        return  bookInterestDao.getLiveBookInterest(userId)
    }

    suspend fun addBookInterest(bookInterest: BookInterest){
        withContext(IO) {
            bookInterestDao.insertOrReplace(bookInterest)
        }
        val oneTimeBookInterestDataUpload =
            OneTimeWorkRequestBuilder<UploadBookInterest>()
                .setConstraints(Constraint.getConnected())
                .build()
        worker.enqueueUniqueWork(Tag.addBookInterestUniqueWorkDatUpload, ExistingWorkPolicy.REPLACE, oneTimeBookInterestDataUpload)
    }


}