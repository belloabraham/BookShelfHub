package com.bookshelfhub.core.data.repos.bookdownload

import androidx.lifecycle.LiveData
import com.bookshelfhub.core.database.AppDatabase
import com.bookshelfhub.core.model.uistate.BookDownloadState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import java.util.*

class BookDownloadStateRepo constructor(
    appDatabase: AppDatabase
) : IBookDownloadStateRepo {

    private val ioDispatcher: CoroutineDispatcher = IO
    private val bookDownloadDao= appDatabase.getBookDownloadStateDao()

    override fun getLiveBookDownloadState(bookId:String): LiveData<Optional<BookDownloadState>> {
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

    override suspend fun deleteDownloadState(bookDownloadState: BookDownloadState){
        return withContext(ioDispatcher){
            bookDownloadDao.delete(bookDownloadState)
        }
    }
}