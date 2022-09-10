package com.bookshelfhub.bookshelfhub.data.repos.publishedbooks

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import com.bookshelfhub.bookshelfhub.data.models.entities.PublishedBook
import com.bookshelfhub.bookshelfhub.data.models.uistate.PublishedBookUiState
import com.google.common.base.Optional

interface IPublishedBooksRepo {
    suspend fun updatePublishedBook(publishedBook: PublishedBook)

    fun getALiveOptionalPublishedBook(bookId: String): LiveData<Optional<PublishedBook>>

    suspend fun getARemotePublishedBook(bookId: String): PublishedBook?
    suspend fun getPublishedBooksByNameOrAuthor(nameOrAuthor:String):List<PublishedBookUiState>
    suspend fun getListOfRemoteUnpublishedBooks(): List<PublishedBook>

    suspend fun getRemotePublishedBooks(): List<PublishedBook>
    suspend fun getTotalNoOfPublishedBooks(): Int
    suspend fun getPublishedBook(bookId: String): Optional<PublishedBook>
    suspend fun getListOfPublishedBooksUiState(): List<PublishedBookUiState>
    suspend  fun getRemotePublishedBooksFrom(fromSerialNo:Int): List<PublishedBook>

    suspend fun updateRecommendedBooksByCategory(category: String, isRecommended: Boolean = true)
    suspend fun updateRecommendedBooksByTag(tag: String, isRecommended: Boolean = true)
    suspend fun addAllPubBooks(pubBooks: List<PublishedBook>)
    suspend fun deleteUnPublishedBookRecords(unPublishedBooks: List<PublishedBook>)

    suspend fun getTrendingBooks(): List<PublishedBookUiState>

    fun getSimilarBooksByCategoryPageSource(category:String, bookId: String): PagingSource<Int, PublishedBookUiState>
    suspend fun getRecommendedBooks(): List<PublishedBookUiState>
    suspend fun getBooksByCategory(category: String): List<PublishedBookUiState>
    fun getBooksByCategoryPageSource(category: String): PagingSource<Int, PublishedBookUiState>
    fun getTrendingBooksPageSource(): PagingSource<Int, PublishedBookUiState>
    fun getRecommendedBooksPageSource(): PagingSource<Int, PublishedBookUiState>
}