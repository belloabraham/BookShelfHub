package com.bookshelfhub.bookshelfhub.data.repos.sources.local

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import com.bookshelfhub.bookshelfhub.data.models.entities.BookInterest
import com.bookshelfhub.bookshelfhub.data.models.entities.PublishedBook
import com.google.common.base.Optional

@Dao
abstract class PublishedBooksDao:BaseDao<PublishedBook>  {
    @Query("SELECT * FROM PublishedBooks WHERE bookId = :isbn")
    abstract suspend  fun getPublishedBook(isbn: String): Optional<PublishedBook>

    @Query("SELECT * FROM PublishedBooks WHERE bookId = :isbn")
    abstract fun getLivePublishedBook(isbn: String): LiveData<Optional<PublishedBook>>


    @Query("UPDATE PublishedBooks SET recommended = :recommend WHERE category =:category")
    abstract suspend  fun updateRecommendedBooksByCategory(category: String, recommend:Boolean)

    @Query("UPDATE PublishedBooks SET recommended = :recommend  WHERE tag = :tag")
    abstract suspend  fun updateRecommendedBooksByTag(tag: String, recommend:Boolean)

    @Query("SELECT * FROM PublishedBooks ORDER BY publishedDate DESC")
    abstract fun getLivePublishedBooks(): LiveData<List<PublishedBook>>

    @Query("SELECT * FROM PublishedBooks ORDER BY publishedDate DESC")
    abstract fun getPublishedBooks(): List<PublishedBook>

   // @Query("SELECT * FROM PublishedBooks WHERE category = :category ORDER BY publishedDate DESC")
   // abstract fun getLiveBooksByCategory(category:String): LiveData<List<PublishedBook>>

    @Query("SELECT * FROM PublishedBooks WHERE category = :category ORDER BY publishedDate DESC")
    abstract suspend fun getBooksByCategory(category:String): List<PublishedBook>

   // @Query("SELECT * FROM PublishedBooks WHERE recommended = :recommend ORDER BY publishedDate DESC")
   // abstract fun getLiveRecommendedBooks(recommend:Boolean=true): LiveData<List<PublishedBook>>

    @Query("SELECT * FROM PublishedBooks WHERE recommended = :recommend ORDER BY publishedDate DESC")
    abstract suspend fun getRecommendedBooks(recommend:Boolean=true): List<PublishedBook>

  //  @Query("SELECT * FROM PublishedBooks ORDER BY totalDownloads DESC LIMIT 100")
    //abstract fun getLiveTrendingBooks(): LiveData<List<PublishedBook>>

    @Query("SELECT * FROM PublishedBooks ORDER BY totalDownloads DESC LIMIT 100")
    abstract suspend fun getTrendingBooks(): List<PublishedBook>

    @Query("SELECT * FROM PublishedBooks WHERE category = :category ORDER BY publishedDate DESC")
    abstract fun getBooksByCategoryPageSource(category:String): PagingSource<Int, PublishedBook>

    @Query("SELECT * FROM PublishedBooks ORDER BY totalDownloads DESC LIMIT 100")
    abstract fun getTrendingBooksPageSource(): PagingSource<Int, PublishedBook>

    @Query("SELECT * FROM PublishedBooks WHERE recommended = :recommend   ORDER BY totalDownloads DESC")
    abstract fun getRecommendedBooksPageSource(recommend:Boolean=true): PagingSource<Int, PublishedBook>
}