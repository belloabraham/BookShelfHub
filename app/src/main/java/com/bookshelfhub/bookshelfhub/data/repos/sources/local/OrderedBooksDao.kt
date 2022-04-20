package com.bookshelfhub.bookshelfhub.data.repos.sources.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bookshelfhub.bookshelfhub.data.models.entities.OrderedBooks
import com.google.common.base.Optional

@Dao
abstract class OrderedBooksDao : BaseDao<OrderedBooks> {

    @Query("SELECT * FROM OrderedBooks WHERE userId = :userId")
    abstract fun getLiveBooksOrdered(userId:String): LiveData<List<OrderedBooks>>

    @Query("SELECT * FROM OrderedBooks WHERE bookId = :isbn")
    abstract fun getLiveOrderedBook(isbn:String): LiveData<OrderedBooks>

    @Query("SELECT * FROM OrderedBooks WHERE userId = :userId")
    abstract suspend fun getOrderedBooks(userId:String): List<OrderedBooks>

    @Query("SELECT * FROM OrderedBooks WHERE bookId =:isbn")
    abstract fun getALiveOrderedBook(isbn:String): LiveData<Optional<OrderedBooks>>

    @Query("SELECT * FROM OrderedBooks WHERE bookId =:isbn")
    abstract suspend fun getAnOrderedBook(isbn:String): OrderedBooks

    @Query("DELETE FROM OrderedBooks")
    abstract fun deleteAllOrderedBooks()

}