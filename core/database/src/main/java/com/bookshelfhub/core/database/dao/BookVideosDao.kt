package com.bookshelfhub.core.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.bookshelfhub.core.model.entities.BookVideo

@Dao
abstract class BookVideosDao : BaseDao<BookVideo> {

    @Query("SELECT * FROM BookVideos WHERE bookId = :bookId")
    abstract fun getLiveListOfBookVideos(bookId: String): LiveData<List<BookVideo>>
}