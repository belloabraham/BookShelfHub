package com.bookshelfhub.bookshelfhub.data.sources.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.bookshelfhub.bookshelfhub.data.models.entities.BookVideo

@Dao
abstract class BookVideosDao : BaseDao<BookVideo> {

    @Query("SELECT * FROM BookVideos WHERE bookId = :bookId")
    abstract fun getLiveListOfBookVideos(bookId: String): LiveData<List<BookVideo>>
}