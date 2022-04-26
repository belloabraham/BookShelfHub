package com.bookshelfhub.bookshelfhub.data.sources.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.bookshelfhub.bookshelfhub.data.models.entities.OrderedBook
import com.bookshelfhub.bookshelfhub.data.models.uistate.OrderedBookUiState
import com.google.common.base.Optional

@Dao
abstract class OrderedBooksDao : BaseDao<OrderedBook> {

    @Query("SELECT * FROM OrderedBooks WHERE userId = :userId")
    abstract fun getLiveBooksOrdered(userId:String): LiveData<List<OrderedBook>>

    @Query("SELECT COUNT(*) FROM OrderedBooks")
    abstract suspend fun getTotalNoOfOrderedBooks(): Int

    @Query("SELECT * FROM OrderedBooks WHERE userId = :userId")
    abstract suspend fun getOrderedBooks(userId:String): List<OrderedBook>

    @Query("SELECT * FROM OrderedBooks WHERE userId = :userId")
    abstract suspend fun getListOfOrderedBooksUiState(userId:String): List<OrderedBookUiState>

    @Query("SELECT * FROM OrderedBooks WHERE userId = :userId")
    abstract fun getLiveListOfOrderedBooksUiState(userId:String): LiveData<List<OrderedBookUiState>>

    @Query("SELECT * FROM OrderedBooks WHERE bookId =:bookId")
    abstract fun getALiveOrderedBook(bookId:String): LiveData<Optional<OrderedBook>>

    @Query("SELECT * FROM OrderedBooks WHERE bookId =:bookId")
    abstract suspend fun getAnOrderedBook(bookId:String): Optional<OrderedBook>

    @Query("DELETE FROM OrderedBooks")
    abstract  fun deleteAllOrderedBooks()

}