package com.bookshelfhub.bookshelfhub.data.repos.publishedbooks

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import com.bookshelfhub.bookshelfhub.data.models.entities.PublishedBook
import com.bookshelfhub.bookshelfhub.data.models.uistate.PublishedBookUiState
import com.bookshelfhub.bookshelfhub.data.sources.local.AppDatabase
import com.bookshelfhub.bookshelfhub.data.sources.remote.RemoteDataFields
import com.bookshelfhub.bookshelfhub.data.sources.remote.IRemoteDataSource
import com.google.common.base.Optional
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PublishedBooksRepo @Inject constructor(
    appDatabase: AppDatabase,
    private val remoteDataSource: IRemoteDataSource,
) : IPublishedBooksRepo {

    private val publishedBooksDao = appDatabase.getPublishedBooksDao()
    private val ioDispatcher: CoroutineDispatcher = IO

     override fun getALiveOptionalPublishedBook(bookId: String): LiveData<Optional<PublishedBook>> {
        return publishedBooksDao.getLivePublishedBook(bookId)
    }

    override suspend fun getARemotePublishedBook(bookId:String): PublishedBook? {
           return remoteDataSource.getDataAsync(
                RemoteDataFields.PUBLISHED_BOOKS_COLL, bookId,
                PublishedBook::class.java
            )
    }

    override suspend fun getListOfPublishedBooksUiState(): List<PublishedBookUiState> {
      return withContext(ioDispatcher) { publishedBooksDao.getListOfPublishedBooksUiState()}
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
        return withContext(ioDispatcher){publishedBooksDao.getPublishedBook(bookId)}
    }

    override suspend fun getPublishedBooksByNameOrAuthor(nameOrAuthor:String): List<PublishedBookUiState>{
        return  withContext(ioDispatcher) {publishedBooksDao.getPublishedBooksByNameOrAuthor(nameOrAuthor)}
    }

    override suspend fun getTotalNoOfPublishedBooks(): Int {
     return  withContext(ioDispatcher) {publishedBooksDao.getTotalNoOfPublishedBooks()}
    }

     override suspend fun updateRecommendedBooksByCategory(category: String, isRecommended:Boolean){
         withContext(ioDispatcher){publishedBooksDao.updateRecommendedBooksByCategory(category, isRecommended)}
    }

     override suspend fun updateRecommendedBooksByTag(tag: String, isRecommended:Boolean){
         withContext(ioDispatcher){publishedBooksDao.updateRecommendedBooksByTag(tag,isRecommended)}
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
        return withContext(ioDispatcher) {publishedBooksDao.getBooksByCategory(category)}
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