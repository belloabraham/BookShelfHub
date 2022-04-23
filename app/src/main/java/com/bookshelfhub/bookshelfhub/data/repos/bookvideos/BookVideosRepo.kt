package com.bookshelfhub.bookshelfhub.data.repos.bookvideos

import androidx.lifecycle.LiveData
import com.bookshelfhub.bookshelfhub.data.models.entities.BookVideo
import com.bookshelfhub.bookshelfhub.data.sources.local.BookVideosDao
import com.bookshelfhub.bookshelfhub.data.sources.remote.IRemoteDataSource
import com.bookshelfhub.bookshelfhub.data.sources.remote.RemoteDataFields
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BookVideosRepo @Inject constructor(
    private val remoteDataSource: IRemoteDataSource,
    private val bookVideosDao: BookVideosDao,
    private val ioDispatcher: CoroutineDispatcher = IO,
) :

    IBookVideosRepo {
    
     override fun getLiveListOfBookVideos(isbn: String): LiveData<List<BookVideo>> {
        return  bookVideosDao.getLiveListOfBookVideos(isbn)
    }

     override suspend fun addBookVideos(bookVideos: List<BookVideo>) {
          withContext(ioDispatcher){bookVideosDao.insertAllOrReplace(bookVideos)}
    }

   override suspend fun getRemoteBookVideos(bookId:String): List<BookVideo> {
    return   withContext(ioDispatcher){remoteDataSource.getListOfDataAsync(
            RemoteDataFields.PUBLISHED_BOOKS_COLL,
            bookId,
            RemoteDataFields.VIDEO_LIST,
            BookVideo::class.java,
        )}
    }
}