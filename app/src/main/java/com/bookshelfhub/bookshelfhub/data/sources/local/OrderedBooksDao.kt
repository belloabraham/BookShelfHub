package com.bookshelfhub.bookshelfhub.data.sources.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.bookshelfhub.bookshelfhub.data.models.entities.OrderedBook
import com.google.common.base.Optional

@Dao
abstract class OrderedBooksDao : BaseDao<OrderedBook> {

    @Query("SELECT * FROM OrderedBooks WHERE userId = :userId")
    abstract fun getLiveBooksOrdered(userId:String): LiveData<List<OrderedBook>>

    @Query("SELECT * FROM OrderedBooks WHERE bookId = :isbn")
    abstract fun getLiveOrderedBook(isbn:String): LiveData<OrderedBook>

    @Query("SELECT * FROM OrderedBooks WHERE userId = :userId")
    abstract suspend fun getOrderedBooks(userId:String): List<OrderedBook>

    @Query("SELECT * FROM OrderedBooks WHERE bookId =:isbn")
    abstract fun getALiveOrderedBook(isbn:String): LiveData<Optional<OrderedBook>>

    @Query("SELECT * FROM OrderedBooks WHERE bookId =:isbn")
    abstract suspend fun getAnOrderedBook(isbn:String): OrderedBook

    @Query("DELETE FROM OrderedBooks")
    abstract  fun deleteAllOrderedBooks()

}