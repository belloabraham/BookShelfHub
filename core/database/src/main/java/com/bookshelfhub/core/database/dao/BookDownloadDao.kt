package com.bookshelfhub.core.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.bookshelfhub.core.model.uistate.BookDownloadState
import java.util.*

@Dao
abstract class BookDownloadDao : BaseDao<BookDownloadState> {

    @Query("SELECT * FROM BookDownloadState WHERE bookId = :bookId")
    abstract fun getLiveBookDownloadState(bookId: String): LiveData<Optional<BookDownloadState>>

}