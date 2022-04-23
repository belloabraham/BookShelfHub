package com.bookshelfhub.bookshelfhub.data.repos.publishedbooks

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import com.bookshelfhub.bookshelfhub.data.models.entities.PublishedBook
import com.bookshelfhub.bookshelfhub.data.sources.local.PublishedBooksDao
import com.bookshelfhub.bookshelfhub.data.sources.remote.RemoteDataFields
import com.bookshelfhub.bookshelfhub.data.sources.remote.IRemoteDataSource
import com.google.common.base.Optional
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PublishedBooksRepo @Inject constructor(
    private val publishedBooksDao: PublishedBooksDao,
    private val remoteDataSource: IRemoteDataSource,
    private val ioDispatcher: CoroutineDispatcher = IO
) :
    IPublishedBooksRepo {

     override fun getALiveOptionalPublishedBook(isbn: String): LiveData<Optional<PublishedBook>> {
        return publishedBooksDao.getLivePublishedBook(isbn)
    }

    override suspend fun getARemotePublishedBook(bookId:String): PublishedBook? {
        return withContext(ioDispatcher) {
            remoteDataSource.getDataAsync(
                RemoteDataFields.PUBLISHED_BOOKS_COLL, bookId,
                PublishedBook::class.java
            )
        }
    }

    override suspend fun getListOfRemoteUnpublishedBooks(): List<PublishedBook> {
        return withContext(ioDispatcher){remoteDataSource.getListOfDataWhereAsync(
                RemoteDataFields.PUBLISHED_BOOKS_COLL,
                RemoteDataFields.PUBLISHED,
                false,
                PublishedBook::class.java
            )
    }
    }

    override suspend  fun getRemotePublishedBooks(): List<PublishedBook> {
        return withContext(ioDispatcher) {
            remoteDataSource.getListOfDataWhereAsync(
                RemoteDataFields.PUBLISHED_BOOKS_COLL,
                whereKey = RemoteDataFields.PUBLISHED, true,
                whereKey2 = RemoteDataFields.APPROVED, true,
                orderBy = RemoteDataFields.SERIAL_NO,
                Query.Direction.ASCENDING,
                PublishedBook::class.java
            )
        }
    }

     override suspend fun getPublishedBook(isbn: String): Optional<PublishedBook> {

        return withContext(ioDispatcher){publishedBooksDao.getPublishedBook(isbn)}
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

     override suspend fun getPublishedBooks(): List<PublishedBook> {
        return withContext(ioDispatcher){publishedBooksDao.getPublishedBooks()}
    }

     override suspend fun getTrendingBooks(): List<PublishedBook> {
        return publishedBooksDao.getTrendingBooks()
    }

     override suspend fun getRecommendedBooks(): List<PublishedBook> {
        return publishedBooksDao.getRecommendedBooks()
    }

   override suspend fun getBooksByCategory(category:String): List<PublishedBook>{
        return publishedBooksDao.getBooksByCategory(category)
    }

     override fun getLivePublishedBooks(): LiveData<List<PublishedBook>> {
        return publishedBooksDao.getLivePublishedBooks()
    }

     override fun getBooksByCategoryPageSource(category:String): PagingSource<Int, PublishedBook> {
        return publishedBooksDao.getBooksByCategoryPageSource(category)
    }

     override fun getTrendingBooksPageSource(): PagingSource<Int, PublishedBook> {
        return publishedBooksDao.getTrendingBooksPageSource()
    }

     override fun getRecommendedBooksPageSource(): PagingSource<Int, PublishedBook> {
        return publishedBooksDao.getRecommendedBooksPageSource()
    }
    
}