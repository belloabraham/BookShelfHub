package com.bookshelfhub.bookshelfhub.data.repos

import androidx.lifecycle.LiveData
import com.bookshelfhub.bookshelfhub.data.models.entities.BookVideos
import com.bookshelfhub.bookshelfhub.data.repos.sources.local.BookVideosDao
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BookVideosRepo @Inject constructor(private val bookVideosDao: BookVideosDao) {
    
     fun getLiveListOfBookVideos(isbn: String): LiveData<List<BookVideos>> {
        return  bookVideosDao.getLiveListOfBookVideos(isbn)
    }

     suspend fun addBookVideos(bookVideos: List<BookVideos>) {
          withContext(IO){bookVideosDao.addBookVideos(bookVideos)}
    }
}