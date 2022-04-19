package com.bookshelfhub.bookshelfhub.data.repos

import androidx.lifecycle.LiveData
import com.bookshelfhub.bookshelfhub.data.models.entities.OrderedBooks
import com.bookshelfhub.bookshelfhub.data.repos.sources.local.OrderedBooksDao
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.IRemoteDataSource
import com.google.common.base.Optional
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import javax.inject.Inject

class OrderedBooksRepo @Inject constructor(private val orderedBooksDao: OrderedBooksDao, private val remoteDataSource: IRemoteDataSource) {

     suspend fun getAnOrderedBook(isbn: String): OrderedBooks {
        return  withContext(IO){ orderedBooksDao.getAnOrderedBook(isbn)}
    }

     fun getLiveOrderedBook(isbn: String): LiveData<OrderedBooks> {
        return orderedBooksDao.getLiveOrderedBook(isbn)
    }

     suspend fun getOrderedBooks(userId: String): List<OrderedBooks> {
        return  withContext(IO){ orderedBooksDao.getOrderedBooks(userId)}
    }

     fun getALiveOptionalOrderedBook(isbn: String): LiveData<Optional<OrderedBooks>> {
        return orderedBooksDao.getALiveOrderedBook(isbn)
    }

     fun deleteAllOrderedBooks() {
        orderedBooksDao.deleteAllOrderedBooks()
    }

     suspend fun addOrderedBooks(OrderedBooks: List<OrderedBooks>){
         withContext(IO){ orderedBooksDao.addOrderedBooks(OrderedBooks)}
    }
     fun getLiveOrderedBooks(userId: String): LiveData<List<OrderedBooks>> {
        return  orderedBooksDao.getLiveBooksOrdered(userId)
    }
}