package com.bookshelfhub.bookshelfhub.data.repos

import androidx.lifecycle.LiveData
import com.bookshelfhub.bookshelfhub.data.models.entities.BookInterest
import com.bookshelfhub.bookshelfhub.data.repos.sources.local.BookInterestDao
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.IRemoteDataSource
import com.bookshelfhub.bookshelfhub.workers.Worker
import com.google.common.base.Optional
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BookInterestRepo @Inject constructor(
    private val bookInterestDao: BookInterestDao,
    private val worker:Worker) {

    suspend fun getBookInterest(userId:String): Optional<BookInterest> {
        return    withContext(IO){
            bookInterestDao.getBookInterest(userId)
        }

    }
    fun getLiveBookInterest(userId:String): LiveData<Optional<BookInterest>> {
        return  bookInterestDao.getLiveBookInterest(userId)
    }

    suspend fun addBookInterest(bookInterest: BookInterest){
        withContext(IO) {
            bookInterestDao.addBookInterest(bookInterest)
        }
    }


}