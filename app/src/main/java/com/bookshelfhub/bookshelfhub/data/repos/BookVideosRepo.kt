package com.bookshelfhub.bookshelfhub.data.repos

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.bookshelfhub.bookshelfhub.data.models.entities.BookVideos
import com.bookshelfhub.bookshelfhub.data.repos.sources.local.BookVideosDao
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.IRemoteDataSource
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.RemoteDataFields
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BookVideosRepo @Inject constructor(
    private val remoteDataSource: IRemoteDataSource,
    private val bookVideosDao: BookVideosDao) {
    
     fun getLiveListOfBookVideos(isbn: String): LiveData<List<BookVideos>> {
        return  bookVideosDao.getLiveListOfBookVideos(isbn)
    }

     suspend fun addBookVideos(bookVideos: List<BookVideos>) {
          withContext(IO){bookVideosDao.insertAllOrReplace(bookVideos)}
    }

   suspend fun getRemoteBookVideos(bookId:String): List<BookVideos> {
    return   remoteDataSource.getListOfDataAsync(
            RemoteDataFields.PUBLISHED_BOOKS_COLL,
            bookId,
            RemoteDataFields.VIDEO_LIST,
            BookVideos::class.java,
        )
    }
}