package com.bookshelfhub.bookshelfhub.data.repos.sources.local

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import com.bookshelfhub.bookshelfhub.data.models.entities.*
import com.bookshelfhub.bookshelfhub.data.models.entities.PaymentTransaction
import com.google.common.base.Optional

@Dao
interface UserDao {

    @Query("DELETE FROM User")
    suspend fun deleteUserRecord()

    @Query("SELECT * FROM User WHERE userId = :userId")
    suspend fun getUser(userId:String): Optional<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUser(user: User)

    @Query("SELECT * FROM User WHERE userId = :userId")
    fun getLiveUser(userId:String): LiveData<User>

}