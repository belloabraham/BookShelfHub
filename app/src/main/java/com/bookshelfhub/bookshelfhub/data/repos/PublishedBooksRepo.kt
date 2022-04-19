package com.bookshelfhub.bookshelfhub.data.repos

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import com.bookshelfhub.bookshelfhub.data.models.entities.PublishedBook
import com.bookshelfhub.bookshelfhub.data.repos.sources.local.PublishedBooksDao
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.RemoteDataFields
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.IRemoteDataSource
import com.google.common.base.Optional
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
         withContext(IO){publishedBooksDao.addAllPubBooks(pubBooks)}
    }

     suspend fun deleteUnPublishedBookRecords(unPublishedBooks : List<PublishedBook>){
         withContext(IO){publishedBooksDao.deleteUnPublishedBookRecords(unPublishedBooks)}
    }

     suspend fun getPublishedBooks(): List<PublishedBook> {
        return withContext(IO){publishedBooksDao.getPublishedBooks()}
    }

     fun getLiveTrendingBooks(): LiveData<List<PublishedBook>> {
        return publishedBooksDao.getLiveTrendingBooks()
    }

     fun getLiveRecommendedBooks(): LiveData<List<PublishedBook>> {
        return publishedBooksDao.getLiveRecommendedBooks()
    }

     fun getLiveBooksByCategory(category:String): LiveData<List<PublishedBook>> {
        return publishedBooksDao.getLiveBooksByCategory(category)
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