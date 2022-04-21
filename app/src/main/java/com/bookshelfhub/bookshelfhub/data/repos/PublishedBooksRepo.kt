package com.bookshelfhub.bookshelfhub.data.repos

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import com.bookshelfhub.bookshelfhub.data.models.entities.PublishedBook
import com.bookshelfhub.bookshelfhub.data.repos.sources.local.PublishedBooksDao
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.RemoteDataFields
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.IRemoteDataSource
import com.google.common.base.Optional
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PublishedBooksRepo @Inject constructor(
    private val publishedBooksDao: PublishedBooksDao,
    private val remoteDataSource: IRemoteDataSource) {

     fun getALiveOptionalPublishedBook(isbn: String): LiveData<Optional<PublishedBook>> {
        return publishedBooksDao.getLivePublishedBook(isbn)
    }

    suspend fun getARemotePublishedBook(bookId:String): PublishedBook? {
       return remoteDataSource.getDataAsync(
            RemoteDataFields.PUBLISHED_BOOKS_COLL, bookId,
            PublishedBook::class.java)
    }

    suspend fun getListOfRemoteUnpublishedBooks(): List<PublishedBook> {
        return remoteDataSource.getListOfDataWhereAsync(
                RemoteDataFields.PUBLISHED_BOOKS_COLL,
                RemoteDataFields.PUBLISHED,
                false,
                PublishedBook::class.java
            )
    }

    suspend  fun getRemotePublishedBooks(): List<PublishedBook> {
      return  remoteDataSource.getListOfDataWhereAsync(
            RemoteDataFields.PUBLISHED_BOOKS_COLL,
            whereKey = RemoteDataFields.PUBLISHED, true,
            whereKey2 = RemoteDataFields.APPROVED, true,
            orderBy = RemoteDataFields.SERIAL_NO,
            Query.Direction.ASCENDING,
            PublishedBook::class.java
        )
    }

     suspend fun getPublishedBook(isbn: String): Optional<PublishedBook> {

        return withContext(IO){publishedBooksDao.getPublishedBook(isbn)}
    }

     suspend fun updateRecommendedBooksByCategory(category: String, isRecommended:Boolean=true){
         withContext(IO){publishedBooksDao.updateRecommendedBooksByCategory(category, isRecommended)}
    }

     suspend fun updateRecommendedBooksByTag(tag: String, isRecommended:Boolean=true){
         withContext(IO){publishedBooksDao.updateRecommendedBooksByTag(tag,isRecommended)}
    }

     suspend fun addAllPubBooks(pubBooks:List<PublishedBook>){
         withContext(IO){publishedBooksDao.insertAllOrReplace(pubBooks)}
    }

     suspend fun deleteUnPublishedBookRecords(unPublishedBooks : List<PublishedBook>){
         withContext(IO){publishedBooksDao.deleteAll(unPublishedBooks)}
    }

     suspend fun getPublishedBooks(): List<PublishedBook> {
        return withContext(IO){publishedBooksDao.getPublishedBooks()}
    }

     suspend fun getTrendingBooks(): List<PublishedBook> {
        return publishedBooksDao.getTrendingBooks()
    }

     suspend fun getRecommendedBooks(): List<PublishedBook> {
        return publishedBooksDao.getRecommendedBooks()
    }

    /* fun getLiveBooksByCategory(category:String): LiveData<List<PublishedBook>> {
        return publishedBooksDao.getLiveBooksByCategory(category)
    }*/

   suspend fun getBooksByCategory(category:String): List<PublishedBook>{
        return publishedBooksDao.getBooksByCategory(category)
    }

     fun getLivePublishedBooks(): LiveData<List<PublishedBook>> {
        return publishedBooksDao.getLivePublishedBooks()
    }

     fun getBooksByCategoryPageSource(category:String): PagingSource<Int, PublishedBook> {
        return publishedBooksDao.getBooksByCategoryPageSource(category)
    }

     fun getTrendingBooksPageSource(): PagingSource<Int, PublishedBook> {
        return publishedBooksDao.getTrendingBooksPageSource()
    }

     fun getRecommendedBooksPageSource(): PagingSource<Int, PublishedBook> {
        return publishedBooksDao.getRecommendedBooksPageSource()
    }
    
}