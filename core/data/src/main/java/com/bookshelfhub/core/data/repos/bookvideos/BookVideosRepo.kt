package com.bookshelfhub.core.data.repos.bookvideos

import androidx.lifecycle.LiveData
import com.bookshelfhub.core.database.AppDatabase
import com.bookshelfhub.core.model.entities.BookVideo
import com.bookshelfhub.core.remote.database.IRemoteDataSource
import com.bookshelfhub.core.remote.database.RemoteDataFields
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BookVideosRepo @Inject constructor(
    appDatabase: AppDatabase,
    private val remoteDataSource: IRemoteDataSource,
    ) :IBookVideosRepo {

    private val ioDispatcher: CoroutineDispatcher = IO
    private val bookVideosDao = appDatabase.getBookVideosDao()

    override fun getLiveListOfBookVideos(isbn: String): LiveData<List<BookVideo>> {
        return  bookVideosDao.getLiveListOfBookVideos(isbn)
    }

     override suspend fun addBookVideos(bookVideos: List<BookVideo>) {
         return withContext(ioDispatcher){bookVideosDao.insertAllOrReplace(bookVideos)}
    }

   override suspend fun getRemoteBookVideos(bookId:String): List<BookVideo> {
    return withContext(ioDispatcher){remoteDataSource.getListOfDataAsync(
            RemoteDataFields.PUBLISHED_BOOKS_COLL,
            bookId,
            RemoteDataFields.VIDEO_LIST,
            BookVideo::class.java,
        )}
    }
}