package com.bookshelfhub.bookshelfhub.services.database.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.User

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUser(user:User)

    @Query("SELECT * FROM user_table")
    fun getUsers(): LiveData<List<User>>
}