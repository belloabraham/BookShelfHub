package com.bookshelfhub.bookshelfhub.data.repos.bookvideos

import androidx.lifecycle.LiveData
import com.bookshelfhub.bookshelfhub.data.models.entities.BookVideo

interface IBookVideosRepo {
    fun getLiveListOfBookVideos(isbn: String): LiveData<List<BookVideo>>

    suspend fun addBookVideos(bookVideos: List<BookVideo>)

    suspend fun getRemoteBookVideos(bookId: String): List<BookVideo>
}