package com.bookshelfhub.bookshelfhub.data.repos.publishedbooks

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import com.bookshelfhub.bookshelfhub.data.models.entities.PublishedBook
import com.google.common.base.Optional

interface IPublishedBooksRepo {
    fun getALiveOptionalPublishedBook(isbn: String): LiveData<Optional<PublishedBook>>

    suspend fun getARemotePublishedBook(bookId: String): PublishedBook?

    suspend fun getListOfRemoteUnpublishedBooks(): List<PublishedBook>

    suspend fun getRemotePublishedBooks(): List<PublishedBook>

    suspend fun getPublishedBook(isbn: String): Optional<PublishedBook>

    suspend fun updateRecommendedBooksByCategory(category: String, isRecommended: Boolean = true)
    suspend fun updateRecommendedBooksByTag(tag: String, isRecommended: Boolean = true)
    suspend fun addAllPubBooks(pubBooks: List<PublishedBook>)
    suspend fun deleteUnPublishedBookRecords(unPublishedBooks: List<PublishedBook>)
    suspend fun getPublishedBooks(): List<PublishedBook>

    suspend fun getTrendingBooks(): List<PublishedBook>

    suspend fun getRecommendedBooks(): List<PublishedBook>

    suspend fun getBooksByCategory(category: String): List<PublishedBook>
    fun getLivePublishedBooks(): LiveData<List<PublishedBook>>
    fun getBooksByCategoryPageSource(category: String): PagingSource<Int, PublishedBook>
    fun getTrendingBooksPageSource(): PagingSource<Int, PublishedBook>
    fun getRecommendedBooksPageSource(): PagingSource<Int, PublishedBook>
}