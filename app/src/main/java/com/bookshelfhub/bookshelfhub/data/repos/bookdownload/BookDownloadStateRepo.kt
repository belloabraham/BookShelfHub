package com.bookshelfhub.bookshelfhub.data.repos.bookdownload

import androidx.lifecycle.LiveData
import com.bookshelfhub.bookshelfhub.data.models.entities.BookVideo
import com.bookshelfhub.bookshelfhub.data.models.uistate.BookDownloadState
import com.bookshelfhub.bookshelfhub.data.sources.local.BookDownloadDao
import com.bookshelfhub.bookshelfhub.data.sources.local.BookVideosDao
import com.bookshelfhub.bookshelfhub.data.sources.remote.IRemoteDataSource
import com.bookshelfhub.bookshelfhub.data.sources.remote.RemoteDataFields
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BookDownloadStateRepo @Inject constructor(
    private val bookDownloadDao: BookDownloadDao,
    private val ioDispatcher: CoroutineDispatcher = IO,
) : IBookDownloadStateRepo {

    override fun getLiveBookDownloadState(bookId:String): LiveData<BookDownloadState> {
        return bookDownloadDao.getLiveBookDownloadState(bookId)
    }

    override suspend fun updatedDownloadState(bookId: String, hasError:Boolean){
        bookDownloadDao.updateBookDownloadState(bookId, hasError)
    }

    override suspend fun updatedDownloadState(bookId: String, progress:Int){
        bookDownloadDao.updateBookDownloadState(bookId, progress)
    }

     override suspend fun addDownloadState(bookDownloadState: BookDownloadState){
         withContext(ioDispatcher){
             bookDownloadDao.insertOrReplace(bookDownloadState)
         }
    }
}