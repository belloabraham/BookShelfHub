package com.bookshelfhub.bookshelfhub.services.database.local

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.*
import com.google.common.base.Optional

interface ILocalDb {
    //Todo User
     suspend fun getUser(userId: String): Optional<User>

     fun getLiveUser(userId: String): LiveData<User>

     suspend fun addUser(user: User)

    //Todo Book Interest
     suspend fun getBookInterest(userId: String): Optional<BookInterest>

     fun getLiveBookInterest(userId: String): LiveData<Optional<BookInterest>>

    suspend fun addBookInterest(bookInterest: BookInterest)

    //Todo Ordered Books
     suspend fun addOrderedBook(orderedBooks: OrderedBooks)
     fun getLiveOrderedBooks(userId: String): LiveData<List<OrderedBooks>>

    //TODO Payment Info
     suspend fun addPaymentInfo(paymentInfo: PaymentInfo)

    //TODO Search History
     suspend fun addStoreSearchHistory(searchHistory: StoreSearchHistory)
     suspend fun addShelfSearchHistory(shelfSearchHistory: ShelfSearchHistory)
     fun getLiveShelfSearchHistory(userId: String): LiveData<List<ShelfSearchHistory>>

     fun getLiveStoreSearchHistory(userId: String): LiveData<List<StoreSearchHistory>>

    //TODO Referrer
     suspend fun getPubReferrer(isbn: String): Optional<PubReferrers>

     suspend fun addPubReferrer(pubReferrers: PubReferrers)
     suspend fun updateRecommendedBooksByCategory(
        category: String,
        isRecommended: Boolean = true
    )

     suspend fun updateRecommendedBooksByTag(tag: String, isRecommended: Boolean = true)
     suspend fun addAllPubBooks(pubBooks: List<PublishedBooks>)
     suspend fun deleteUnPublishedBookRecords(isbnList: List<String>)
     suspend fun getPublishedBooks(): List<PublishedBooks>

     fun getLiveTrendingBooks(): LiveData<List<PublishedBooks>>

     fun getLiveRecommendedBooks(): LiveData<List<PublishedBooks>>

     fun getLiveBooksByCategory(category: String): LiveData<List<PublishedBooks>>

     fun getLivePublishedBooks(): LiveData<List<PublishedBooks>>

     fun getBooksByCategoryPageSource(category: String): PagingSource<Int, PublishedBooks>

     fun getTrendingBooksPageSource(): PagingSource<Int, PublishedBooks>

     fun getRecommendedBooksPageSource(): PagingSource<Int, PublishedBooks>
}