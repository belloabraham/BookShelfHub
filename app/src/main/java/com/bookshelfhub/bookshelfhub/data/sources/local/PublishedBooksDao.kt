package com.bookshelfhub.bookshelfhub.data.sources.local

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import com.bookshelfhub.bookshelfhub.data.models.entities.PublishedBook
import com.bookshelfhub.bookshelfhub.data.models.uistate.PublishedBookUiState
import com.google.common.base.Optional

@Dao
abstract class PublishedBooksDao:BaseDao<PublishedBook>  {
    @Query("SELECT * FROM PublishedBooks WHERE bookId = :bookId")
    abstract suspend  fun getPublishedBook(bookId: String): Optional<PublishedBook>

    @Query("SELECT * FROM PublishedBooks WHERE bookId = :bookId")
    abstract fun getLivePublishedBook(bookId: String): LiveData<Optional<PublishedBook>>

    @Query("SELECT COUNT(*) FROM PublishedBooks")
    abstract suspend fun getTotalNoOfPublishedBooks(): Int

    @Query("UPDATE PublishedBooks SET recommended = :recommend WHERE category =:category")
    abstract suspend  fun updateRecommendedBooksByCategory(category: String, recommend:Boolean)

    @Query("UPDATE PublishedBooks SET recommended = :recommend  WHERE tag = :tag")
    abstract suspend  fun updateRecommendedBooksByTag(tag: String, recommend:Boolean)

    @Query("SELECT * FROM PublishedBooks ORDER BY publishedDate DESC")
    abstract suspend fun getListOfPublishedBooksUiState(): List<PublishedBookUiState>

    @Query("SELECT * FROM PublishedBooks WHERE category = :category ORDER BY publishedDate DESC")
    abstract suspend fun getBooksByCategory(category:String): List<PublishedBookUiState>

    @Query("SELECT * FROM PublishedBooks WHERE recommended = :recommend ORDER BY publishedDate DESC")
    abstract suspend fun getRecommendedBooks(recommend:Boolean=true): List<PublishedBookUiState>

    @Query("SELECT * FROM PublishedBooks ORDER BY totalDownloads DESC LIMIT 100")
    abstract suspend fun getTrendingBooks(): List<PublishedBookUiState>

    @Query("SELECT * FROM PublishedBooks WHERE category = :category ORDER BY publishedDate DESC")
    abstract fun getBooksByCategoryPageSource(category:String): PagingSource<Int, PublishedBookUiState>

    @Query("SELECT * FROM PublishedBooks ORDER BY totalDownloads DESC LIMIT 100")
    abstract fun getTrendingBooksPageSource(): PagingSource<Int, PublishedBookUiState>

    @Query("SELECT * FROM PublishedBooks WHERE recommended = :recommend   ORDER BY totalDownloads DESC")
    abstract fun getRecommendedBooksPageSource(recommend:Boolean=true): PagingSource<Int, PublishedBookUiState>
}