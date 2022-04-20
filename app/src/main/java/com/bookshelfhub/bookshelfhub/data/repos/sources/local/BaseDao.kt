package com.bookshelfhub.bookshelfhub.data.repos.sources.local

import androidx.room.*

@Dao
interface BaseDao<T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(entity: T)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllOrReplace(entities: List<T>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllOrIgnore(entity: List<T>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnore(entity: T)

    @Delete
    suspend fun deleteAll(entities: List<T>)

    @Delete
    suspend fun delete(entity: T)

}