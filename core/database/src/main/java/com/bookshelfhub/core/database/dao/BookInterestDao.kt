package com.bookshelfhub.core.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.bookshelfhub.core.model.entities.BookInterest
import java.util.*

@Dao
abstract class BookInterestDao : BaseDao<BookInterest> {
    @Query("SELECT * FROM BookInterests WHERE userId = :userId")
    abstract suspend fun getBookInterest(userId:String): Optional<BookInterest>

    @Query("SELECT * FROM BookInterests WHERE userId = :userId")
    abstract fun getLiveBookInterest(userId:String): LiveData<Optional<BookInterest>>
}