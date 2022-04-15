package com.bookshelfhub.bookshelfhub.data.repos.sources.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bookshelfhub.bookshelfhub.data.models.entities.OrderedBooks
import com.google.common.base.Optional

@Dao
interface OrderedBooksDao {

    @Query("SELECT * FROM OrderedBooks WHERE userId = :userId")
    fun getLiveBooksOrdered(userId:String): LiveData<List<OrderedBooks>>

    @Query("SELECT * FROM OrderedBooks WHERE bookId = :isbn")
    fun getLiveOrderedBook(isbn:String): LiveData<OrderedBooks>

    @Query("SELECT * FROM OrderedBooks WHERE userId = :userId")
    suspend fun getOrderedBooks(userId:String): List<OrderedBooks>

    @Query("SELECT * FROM OrderedBooks WHERE bookId =:isbn")
    fun getALiveOrderedBook(isbn:String): LiveData<Optional<OrderedBooks>>

    @Query("SELECT * FROM OrderedBooks WHERE bookId =:isbn")
    suspend fun getAnOrderedBook(isbn:String): OrderedBooks

    @Query("DELETE FROM OrderedBooks")
    fun deleteAllOrderedBooks()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addOrderedBooks(OrderedBooks: List<OrderedBooks>)

}