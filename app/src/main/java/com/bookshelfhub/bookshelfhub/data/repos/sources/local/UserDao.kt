package com.bookshelfhub.bookshelfhub.data.repos.sources.local

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import com.bookshelfhub.bookshelfhub.data.models.entities.*
import com.bookshelfhub.bookshelfhub.data.models.entities.PaymentTransaction
import com.google.common.base.Optional

@Dao
abstract class UserDao :BaseDao<User> {

    @Query("DELETE FROM Users")
    abstract suspend  fun deleteUserRecord()

    @Query("SELECT * FROM Users WHERE userId = :userId")
    abstract suspend  fun getUser(userId:String): Optional<User>

    @Query("SELECT * FROM Users WHERE userId = :userId")
    abstract fun getLiveUser(userId:String): LiveData<User>

}