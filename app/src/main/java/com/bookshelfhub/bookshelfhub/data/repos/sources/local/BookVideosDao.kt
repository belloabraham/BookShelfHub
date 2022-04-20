package com.bookshelfhub.bookshelfhub.data.repos.sources.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bookshelfhub.bookshelfhub.data.models.entities.BookInterest
import com.bookshelfhub.bookshelfhub.data.models.entities.BookVideos
import com.google.common.base.Optional

@Dao
abstract class BookVideosDao : BaseDao<BookVideos> {

    @Query("SELECT * FROM BookVideos WHERE isbn = :isbn")
    abstract fun getLiveListOfBookVideos(isbn: String): LiveData<List<BookVideos>>
}