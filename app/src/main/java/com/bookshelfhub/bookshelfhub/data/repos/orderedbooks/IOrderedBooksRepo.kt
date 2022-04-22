package com.bookshelfhub.bookshelfhub.data.repos.orderedbooks

import androidx.lifecycle.LiveData
import com.bookshelfhub.bookshelfhub.data.models.entities.OrderedBook
import com.google.common.base.Optional
import com.google.firebase.firestore.Query

interface IOrderedBooksRepo {
    suspend fun getAnOrderedBook(isbn: String): OrderedBook
    fun getLiveOrderedBook(isbn: String): LiveData<OrderedBook>

    suspend fun getOrderedBooks(userId: String): List<OrderedBook>
    fun getALiveOptionalOrderedBook(isbn: String): LiveData<Optional<OrderedBook>>
    fun deleteAllOrderedBooks()

    suspend fun getRemoteListOfOrderedBooks(
        userId: String,
        lastOrderedBookBySN:Long,
        direction: Query.Direction = Query.Direction.ASCENDING ): List<OrderedBook>

    suspend fun addOrderedBooks(OrderedBooks: List<OrderedBook>)
    fun getLiveOrderedBooks(userId: String): LiveData<List<OrderedBook>>
}