package com.bookshelfhub.core.data.repos.ordered_books

import androidx.lifecycle.LiveData
import com.bookshelfhub.core.model.entities.OrderedBook
import com.bookshelfhub.core.model.uistate.OrderedBookUiState
import com.google.firebase.firestore.Query
import java.util.*

interface IOrderedBooksRepo {

    suspend fun getAnOrderedBook(bookId: String): Optional<OrderedBook>
    fun getLiveListOfOrderedBooksUiState(userId: String): LiveData<List<OrderedBookUiState>>
    suspend fun getOrderedBooks(userId: String): List<OrderedBook>
    suspend fun addAnOrderedBook(orderedBook: OrderedBook)
    fun getALiveOptionalOrderedBook(isbn: String): LiveData<Optional<OrderedBook>>
    suspend fun deleteAllOrderedBooks()
    suspend fun getTotalNoOfOrderedBooks(): Int
    suspend fun getRemoteListOfOrderedBooks(
        userId: String,
        lastOrderedBookSN:Long,
        direction: Query.Direction = Query.Direction.ASCENDING ): List<OrderedBook>
    suspend fun addOrderedBooks(OrderedBooks: List<OrderedBook>)
    fun getLiveOrderedBooks(userId: String): LiveData<List<OrderedBook>>
}