package com.bookshelfhub.bookshelfhub.services.database.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.UserRecord
import com.google.common.base.Optional

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUser(userRecord:UserRecord)

    @Query("SELECT * FROM user LIMIT 1")
    fun getUser(): Optional<UserRecord>

    @Query("SELECT * FROM user LIMIT 1")
    fun getLiveUser(): LiveData<UserRecord>
}