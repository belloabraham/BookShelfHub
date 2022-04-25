package com.bookshelfhub.bookshelfhub.data.sources.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.bookshelfhub.bookshelfhub.data.models.entities.BookVideo
import com.bookshelfhub.bookshelfhub.data.models.uistate.BookDownloadState

@Dao
abstract class BookDownloadDao : BaseDao<BookDownloadState> {

    @Query("SELECT * FROM BookDownloadStates WHERE bookId = :bookId LIMIT 1")
    abstract fun getLiveBookDownloadState(bookId: String): LiveData<BookDownloadState>

    @Query("UPDATE BookDownloadStates SET hasError = :hasError WHERE bookId = :bookId")
    abstract suspend fun updateBookDownloadState(bookId: String, hasError:Boolean)

    @Query("UPDATE BookDownloadStates SET progress = :progress WHERE bookId = :bookId")
    abstract suspend fun updateBookDownloadState(bookId: String, progress:Long)


}