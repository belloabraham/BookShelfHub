package com.bookshelfhub.bookshelfhub.data.repos.sources.local

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import com.bookshelfhub.bookshelfhub.data.models.entities.BookInterest
import com.bookshelfhub.bookshelfhub.data.models.entities.PublishedBook
import com.google.common.base.Optional

@Dao
interface PublishedBooksDao {
    @Query("SELECT * FROM PublishedBook WHERE bookId = :isbn")
    suspend fun getPublishedBook(isbn: String): Optional<PublishedBook>

    @Query("SELECT * FROM PublishedBook WHERE bookId = :isbn")
    fun getLivePublishedBook(isbn: String): LiveData<Optional<PublishedBook>>

    @Delete
    suspend fun deleteUnPublishedBookRecords(publishedBooks: List<PublishedBook>)


    @Query("UPDATE PublishedBook SET recommended = :recommend WHERE category =:category")
    suspend fun updateRecommendedBooksByCategory(category: String, recommend:Boolean)

    @Query("UPDATE PublishedBook SET recommended = :recommend  WHERE tag = :tag")
    suspend fun updateRecommendedBooksByTag(tag: String, recommend:Boolean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAllPubBooks(publishedBooks: List<PublishedBook>)

    @Query("SELECT * FROM PublishedBook ORDER BY publishedDate DESC")
    fun getLivePublishedBooks(): LiveData<List<PublishedBook>>

    @Query("SELECT * FROM PublishedBook ORDER BY publishedDate DESC")
    fun getPublishedBooks(): List<PublishedBook>

    @Query("SELECT * FROM PublishedBook WHERE category = :category ORDER BY publishedDate DESC")
    fun getLiveBooksByCategory(category:String): LiveData<List<PublishedBook>>

    @Query("SELECT * FROM PublishedBook WHERE recommended = :recommend ORDER BY publishedDate DESC")
    fun getLiveRecommendedBooks(recommend:Boolean=true): LiveData<List<PublishedBook>>

    @Query("SELECT * FROM PublishedBook ORDER BY totalDownloads DESC LIMIT 100")
    fun getLiveTrendingBooks(): LiveData<List<PublishedBook>>

    @Query("SELECT * FROM PublishedBook WHERE category = :category ORDER BY publishedDate DESC")
    fun getBooksByCategoryPageSource(category:String): PagingSource<Int, PublishedBook>

    @Query("SELECT * FROM PublishedBook ORDER BY totalDownloads DESC LIMIT 100")
    fun getTrendingBooksPageSource(): PagingSource<Int, PublishedBook>

    @Query("SELECT * FROM PublishedBook WHERE recommended = :recommend   ORDER BY totalDownloads DESC")
    fun getRecommendedBooksPageSource(recommend:Boolean=true): PagingSource<Int, PublishedBook>
}