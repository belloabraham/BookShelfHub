package com.bookshelfhub.bookshelfhub.data.sources.local

import androidx.room.*

@Dao
interface BaseDao<T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(entity: T)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllOrReplace(entities: List<T>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllOrIgnore(entity: List<T>)

    @Delete
    suspend fun deleteAll(entities: List<T>)

    @Delete
    suspend fun delete(entity: T)

}