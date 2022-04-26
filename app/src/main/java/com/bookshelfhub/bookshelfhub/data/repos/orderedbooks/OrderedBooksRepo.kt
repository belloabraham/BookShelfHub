package com.bookshelfhub.bookshelfhub.data.repos.orderedbooks

import androidx.lifecycle.LiveData
import com.bookshelfhub.bookshelfhub.data.models.entities.OrderedBook
import com.bookshelfhub.bookshelfhub.data.sources.local.OrderedBooksDao
import com.bookshelfhub.bookshelfhub.data.sources.remote.IRemoteDataSource
import com.bookshelfhub.bookshelfhub.data.sources.remote.RemoteDataFields
import com.google.common.base.Optional
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import javax.inject.Inject

class OrderedBooksRepo @Inject constructor(
    private val orderedBooksDao: OrderedBooksDao,
    private val remoteDataSource: IRemoteDataSource,
    private val ioDispatcher: CoroutineDispatcher = IO
) :
    IOrderedBooksRepo {



     override suspend fun getAnOrderedBook(bookId: String): Optional<OrderedBook> {
        return  withContext(ioDispatcher){ orderedBooksDao.getAnOrderedBook(bookId)}
    }

    override suspend fun getRemoteListOfOrderedBooks(
        userId: String,
        lastOrderedBookBySN:Long,
        direction:Query.Direction): List<OrderedBook> {
          return  withContext(ioDispatcher){
              remoteDataSource.getListOfDataAsync(
                RemoteDataFields.USERS_COLL,
                userId,
                RemoteDataFields.ORDERED_BOOKS_COLL,
                RemoteDataFields.SERIAL_NO,
                direction,
                lastOrderedBookBySN,
                OrderedBook::class.java
            )}
    }


     override fun getLiveOrderedBook(isbn: String): LiveData<Optional<OrderedBook>> {
        return orderedBooksDao.getALiveOrderedBook(isbn)
    }

     override suspend fun getOrderedBooks(userId: String): List<OrderedBook> {
        return  withContext(ioDispatcher){ orderedBooksDao.getOrderedBooks(userId)}
    }

     override fun getALiveOptionalOrderedBook(isbn: String): LiveData<Optional<OrderedBook>> {
        return orderedBooksDao.getALiveOrderedBook(isbn)
    }

     override fun deleteAllOrderedBooks() {
        orderedBooksDao.deleteAllOrderedBooks()
    }

     override suspend fun addOrderedBooks(OrderedBooks: List<OrderedBook>){
         withContext(ioDispatcher){ orderedBooksDao.insertAllOrIgnore(OrderedBooks)}
    }
     override fun getLiveOrderedBooks(userId: String): LiveData<List<OrderedBook>> {
        return  orderedBooksDao.getLiveBooksOrdered(userId)
    }
}