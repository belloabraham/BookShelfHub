package com.bookshelfhub.bookshelfhub.services.database.local

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.*
import com.bookshelfhub.bookshelfhub.workers.UnPublishedBooks
import com.google.common.base.Optional

interface ILocalDb {

     suspend fun updateReview(isbn: String, isVerified:Boolean)
     suspend fun deleteAllReviews()
     suspend fun deleteUserRecord()
     suspend fun getUserReview(isbn:String): Optional<UserReview>
     suspend fun addUserReview(userReview: UserReview)
     fun getLiveUserReview(isbn:String): LiveData<Optional<UserReview>>
     suspend fun getPublishedBook(isbn: String): PublishedBooks
     fun getLiveBookmarks(userId: String, deleted: Boolean = false): LiveData<List<Bookmark>>
     suspend fun getListOfCartItems(userId: String):List<Cart>
     fun getLiveListOfCartItems(userId: String):LiveData<List<Cart>>
     suspend fun getDeletedBookmarks(userId: String, deleted: Boolean=true):List<Bookmark>
     suspend fun addBookmarkList(bookmarks: List<Bookmark>)
     suspend fun addToCart(cart:Cart)
     suspend fun deleteFromCart(cart: Cart)
     suspend fun addBookmark(bookmark: Bookmark)
     suspend fun deleteBookmarks(bookmarks: List<Bookmark>)
     suspend fun getLocalBookmarks(userId: String, uploaded:Boolean=false, deleted: Boolean=false): List<Bookmark>
     suspend fun getBookmarks(userId: String, deleted: Boolean = false):List<Bookmark>
     fun getLiveTotalCartItemsNo(userId: String): LiveData<Int>
     suspend fun getUser(userId: String): Optional<User>
     fun getLiveUser(userId: String): LiveData<User>
     suspend fun addUser(user: User)
     suspend fun getBookInterest(userId: String): Optional<BookInterest>
     fun getLiveBookInterest(userId: String): LiveData<Optional<BookInterest>>
     suspend fun addBookInterest(bookInterest: BookInterest)
     suspend fun addOrderedBook(orderedBooks: OrderedBooks)
     fun getLiveOrderedBooks(userId: String): LiveData<List<OrderedBooks>>
     suspend fun addStoreSearchHistory(searchHistory: StoreSearchHistory)
     suspend fun addShelfSearchHistory(shelfSearchHistory: ShelfSearchHistory)
     fun getLiveShelfSearchHistory(userId: String): LiveData<List<ShelfSearchHistory>>
     fun getLiveStoreSearchHistory(userId: String): LiveData<List<StoreSearchHistory>>
     suspend fun getPubReferrer(isbn: String): Optional<PubReferrers>
     suspend fun addPubReferrer(pubReferrers: PubReferrers)
     suspend fun updateRecommendedBooksByCategory(category: String, isRecommended: Boolean = true)
     suspend fun updateRecommendedBooksByTag(tag: String, isRecommended: Boolean = true)
     suspend fun addAllPubBooks(pubBooks: List<PublishedBooks>)
     suspend fun deleteUnPublishedBookRecords(unPublishedBooks: List<PublishedBooks>)
     suspend fun getPublishedBooks(): List<PublishedBooks>
     fun getLiveTrendingBooks(): LiveData<List<PublishedBooks>>
     fun getLiveRecommendedBooks(): LiveData<List<PublishedBooks>>
     fun getLiveBooksByCategory(category: String): LiveData<List<PublishedBooks>>
     fun getLivePublishedBooks(): LiveData<List<PublishedBooks>>
     fun getBooksByCategoryPageSource(category: String): PagingSource<Int, PublishedBooks>
     fun getTrendingBooksPageSource(): PagingSource<Int, PublishedBooks>
     fun getRecommendedBooksPageSource(): PagingSource<Int, PublishedBooks>
}