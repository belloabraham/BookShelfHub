package com.bookshelfhub.bookshelfhub.data.sources.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.bookshelfhub.bookshelfhub.data.models.entities.BookVideo

@Dao
abstract class BookVideosDao : BaseDao<BookVideo> {

    @Query("SELECT * FROM BookVideos WHERE isbn = :isbn")
    abstract fun getLiveListOfBookVideos(isbn: String): LiveData<List<BookVideo>>
}