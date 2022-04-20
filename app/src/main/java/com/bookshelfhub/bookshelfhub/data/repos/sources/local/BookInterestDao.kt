package com.bookshelfhub.bookshelfhub.data.repos.sources.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bookshelfhub.bookshelfhub.data.models.entities.BookInterest
import com.google.common.base.Optional

@Dao
abstract class BookInterestDao : BaseDao<BookInterest> {
    @Query("SELECT * FROM BookInterest WHERE userId = :userId")
    abstract suspend fun getBookInterest(userId:String): Optional<BookInterest>

    @Query("SELECT * FROM BookInterest WHERE userId = :userId")
    abstract fun getLiveBookInterest(userId:String): LiveData<Optional<BookInterest>>
}