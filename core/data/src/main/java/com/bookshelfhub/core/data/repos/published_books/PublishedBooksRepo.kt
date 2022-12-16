package com.bookshelfhub.core.data.repos.published_books

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import com.bookshelfhub.core.database.AppDatabase
import com.bookshelfhub.core.model.entities.PublishedBook
import com.bookshelfhub.core.model.uistate.PublishedBookUiState
import com.bookshelfhub.core.remote.database.IRemoteDataSource
import com.bookshelfhub.core.remote.database.RemoteDataFields
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class PublishedBooksRepo @Inject constructor(
    appDatabase: AppDatabase,
    private val remoteDataSource: IRemoteDataSource,
) : IPublishedBooksRepo {

    private val publishedBooksDao = appDatabase.getPublishedBooksDao()
    private val ioDispatcher: CoroutineDispatcher = IO

     override suspend fun updatePublishedBook(publishedBook: PublishedBook){
        withContext(ioDispatcher) {
            publishedBooksDao.insertOrReplace(publishedBook)
        }
    }

     override fun getALiveOptionalPublishedBook(bookId: String): LiveData<Optional<PublishedBook>> {
        return publishedBooksDao.getLivePublishedBook(bookId)
    }

    override fun updateBookTotalDownloadsByOneAsync(bookId:String, field:String, value:Any): Task<Void> {
        return remoteDataSource.updateDocDataAsync(RemoteDataFields.PUBLISHED_BOOKS_COLL, bookId, field, value)
    }

    override suspend fun getARemotePublishedBook(bookId:String): PublishedBook? {
           return remoteDataSource.getDataAsync(
                RemoteDataFields.PUBLISHED_BOOKS_COLL, bookId,
                PublishedBook::class.java
            )
    }

    override suspend fun getListOfPublishedBooksUiState(): List<PublishedBookUiState> {
      return withContext(ioDispatcher) {
          publishedBooksDao.getListOfPublishedBooksUiState()
      }
    }

    override suspend fun getListOfRemoteUnpublishedBooks(): List<PublishedBook> {
          return  remoteDataSource.getListOfDataWhereAsync(
                RemoteDataFields.PUBLISHED_BOOKS_COLL,
                RemoteDataFields.PUBLISHED,
                false,
                PublishedBook::class.java
            )
    }

    override suspend  fun getRemotePublishedBooks(): List<PublishedBook> {
        return   remoteDataSource.getListOfDataWhereAsync(
                RemoteDataFields.PUBLISHED_BOOKS_COLL,
                whereKey = RemoteDataFields.PUBLISHED, true,
                whereKey2 = RemoteDataFields.APPROVED, true,
                orderBy = RemoteDataFields.SERIAL_NO,
                Query.Direction.ASCENDING,
                PublishedBook::class.java
            )
    }

    override suspend  fun getRemotePublishedBooksFrom(fromSerialNo:Int): List<PublishedBook> {
        return remoteDataSource.getListOfDataWhereAsync(
                RemoteDataFields.PUBLISHED_BOOKS_COLL,
                whereKey = RemoteDataFields.PUBLISHED, true,
                whereKey2 = RemoteDataFields.APPROVED, true,
                orderBy = RemoteDataFields.SERIAL_NO,
                fromSerialNo,
                Query.Direction.ASCENDING,
                PublishedBook::class.java
            )
    }

     override suspend fun getPublishedBook(bookId: String): Optional<PublishedBook> {
        return withContext(ioDispatcher){
            publishedBooksDao.getPublishedBook(bookId)
        }
    }

    override suspend fun getPublishedBooksByNameOrAuthor(nameOrAuthor:String): List<PublishedBookUiState>{
        return  withContext(ioDispatcher) {
            publishedBooksDao.getPublishedBooksByNameOrAuthor(nameOrAuthor)
        }
    }

    override suspend fun getTotalNoOfPublishedBooks(): Int {
     return  withContext(ioDispatcher) {
         publishedBooksDao.getTotalNoOfPublishedBooks()
     }
    }

     override suspend fun updateRecommendedBooksByCategory(category: String, isRecommended:Boolean){
         withContext(ioDispatcher){
             publishedBooksDao.updateRecommendedBooksByCategory(category, isRecommended)
         }

    }

     override suspend fun addAllPubBooks(pubBooks:List<PublishedBook>){
         withContext(ioDispatcher){publishedBooksDao.insertAllOrReplace(pubBooks)}
    }

     override suspend fun deleteUnPublishedBookRecords(unPublishedBooks : List<PublishedBook>){
         withContext(ioDispatcher){publishedBooksDao.deleteAll(unPublishedBooks)}
    }


     override suspend fun getTrendingBooks(): List<PublishedBookUiState> {
        return withContext(ioDispatcher) {publishedBooksDao.getTrendingBooks()}
    }

     override suspend fun getRecommendedBooks(): List<PublishedBookUiState> {
        return withContext(ioDispatcher) {publishedBooksDao.getRecommendedBooks()}
    }

   override suspend fun getBooksByCategory(category:String): List<PublishedBookUiState>{
        return withContext(ioDispatcher) {
            publishedBooksDao.getBooksByCategory(category)
        }
    }

     override fun getBooksByCategoryPageSource(category:String): PagingSource<Int, PublishedBookUiState> {
        return publishedBooksDao.getBooksByCategoryPageSource(category)
    }

    override fun getSimilarBooksByCategoryPageSource(category:String, bookId: String): PagingSource<Int, PublishedBookUiState> {
        return publishedBooksDao.getSimilarBooksByCategoryPageSource(category, bookId)
    }

     override fun getTrendingBooksPageSource(): PagingSource<Int, PublishedBookUiState> {
        return publishedBooksDao.getTrendingBooksPageSource()
    }

     override fun getRecommendedBooksPageSource(): PagingSource<Int, PublishedBookUiState> {
        return publishedBooksDao.getRecommendedBooksPageSource()
    }
}