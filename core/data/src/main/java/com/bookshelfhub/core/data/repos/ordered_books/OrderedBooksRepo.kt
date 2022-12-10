package com.bookshelfhub.core.data.repos.ordered_books

import androidx.lifecycle.LiveData
import com.bookshelfhub.core.database.AppDatabase
import com.bookshelfhub.core.model.entities.OrderedBook
import com.bookshelfhub.core.model.uistate.OrderedBookUiState
import com.bookshelfhub.core.remote.database.IRemoteDataSource
import com.bookshelfhub.core.remote.database.RemoteDataFields
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class OrderedBooksRepo @Inject constructor(
    appDatabase: AppDatabase,
    private val remoteDataSource: IRemoteDataSource,
) :IOrderedBooksRepo {

    private val orderedBooksDao = appDatabase.getOrderedBooksDao()
    private val ioDispatcher: CoroutineDispatcher = IO

     override suspend fun getAnOrderedBook(bookId: String): Optional<OrderedBook> {
        return  withContext(ioDispatcher){ orderedBooksDao.getAnOrderedBook(bookId)}
    }

    override suspend fun getTotalNoOfOrderedBooks(): Int {
       return withContext(ioDispatcher){ orderedBooksDao.getTotalNoOfOrderedBooks()}
    }

    override fun getLiveListOfOrderedBooksUiState(userId: String): LiveData<List<OrderedBookUiState>> {
        return orderedBooksDao.getLiveListOfOrderedBooksUiState(userId)
    }

    override suspend fun getRemoteOrderedBooks(userId: String): List<OrderedBook> {
       return remoteDataSource.getListOfDataAsync(
            RemoteDataFields.USERS_COLL,
            userId,
           RemoteDataFields.ORDERED_BOOKS_COLL,
           OrderedBook::class.java
       )
    }

    override suspend fun getRemoteListOfOrderedBooks(
        userId: String,
        lastOrderedBookSN:Long,
        direction:Query.Direction): List<OrderedBook> {
          return  remoteDataSource.getListOfDataAsync(
                RemoteDataFields.USERS_COLL,
                userId,
                RemoteDataFields.ORDERED_BOOKS_COLL,
                orderBy = RemoteDataFields.SERIAL_NO,
                direction,
                 startAfter =  lastOrderedBookSN,
                OrderedBook::class.java
            )
    }


   override suspend fun addAnOrderedBook(orderedBook: OrderedBook){
       return withContext(ioDispatcher){
           orderedBooksDao.insertOrReplace(orderedBook)
       }
    }

   override suspend fun getOrderedBooks(userId: String): List<OrderedBook> {
        return  withContext(ioDispatcher){
            orderedBooksDao.getOrderedBooks(userId)
        }
   }

    override fun getALiveOptionalOrderedBook(isbn: String): LiveData<Optional<OrderedBook>> {
        return orderedBooksDao.getALiveOrderedBook(isbn)
    }

     override suspend fun deleteAllOrderedBooks() {
        return withContext(ioDispatcher){
             orderedBooksDao.deleteAllOrderedBooks()
         }
    }

     override suspend fun addOrderedBooks(OrderedBooks: List<OrderedBook>){
        return withContext(ioDispatcher){
             orderedBooksDao.insertAllOrIgnore(OrderedBooks)
         }
    }
     override fun getLiveOrderedBooks(userId: String): LiveData<List<OrderedBook>> {
        return  orderedBooksDao.getLiveBooksOrdered(userId)
    }
}