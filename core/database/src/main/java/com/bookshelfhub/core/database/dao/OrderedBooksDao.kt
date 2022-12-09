package com.bookshelfhub.core.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.bookshelfhub.core.model.entities.OrderedBook
import com.bookshelfhub.core.model.uistate.OrderedBookUiState
import java.util.*

@Dao
abstract class OrderedBooksDao : BaseDao<OrderedBook> {

    @Query("SELECT * FROM OrderedBooks WHERE userId = :userId")
    abstract fun getLiveBooksOrdered(userId:String): LiveData<List<OrderedBook>>

    @Query("SELECT COUNT(*) FROM OrderedBooks")
    abstract suspend fun getTotalNoOfOrderedBooks(): Int

    @Query("SELECT * FROM OrderedBooks WHERE userId = :userId")
    abstract suspend fun getOrderedBooks(userId:String): List<OrderedBook>

    @Query("SELECT * FROM OrderedBooks WHERE userId = :userId")
    abstract fun getLiveListOfOrderedBooksUiState(userId:String): LiveData<List<OrderedBookUiState>>

    @Query("SELECT * FROM OrderedBooks WHERE bookId =:bookId")
    abstract fun getALiveOrderedBook(bookId:String): LiveData<Optional<OrderedBook>>

    @Query("SELECT * FROM OrderedBooks WHERE bookId =:bookId")
    abstract suspend fun getAnOrderedBook(bookId:String): Optional<OrderedBook>

    @Query("DELETE FROM OrderedBooks")
    abstract suspend fun deleteAllOrderedBooks()

}