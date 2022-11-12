package com.bookshelfhub.core.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.bookshelfhub.core.model.entities.User
import java.util.*

@Dao
abstract class UserDao : BaseDao<User> {

    @Query("DELETE FROM Users")
    abstract suspend  fun deleteUserRecord()

    @Query("SELECT * FROM Users WHERE userId = :userId")
    abstract suspend  fun getUser(userId:String): Optional<User>

    @Query("SELECT * FROM Users WHERE userId = :userId")
    abstract fun getLiveUser(userId:String): LiveData<User>

}