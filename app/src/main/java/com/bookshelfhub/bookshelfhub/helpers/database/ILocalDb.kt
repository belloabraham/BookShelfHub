package com.bookshelfhub.bookshelfhub.helpers.database

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import com.bookshelfhub.bookshelfhub.helpers.database.room.entities.*
import com.google.common.base.Optional

interface ILocalDb {

     fun getLiveListOfBookVideos(isbn: String): LiveData<List<BookVideos>>
     suspend fun addBookVideos(bookVideos: List<BookVideos>)
     suspend fun getAnOrderedBook(isbn:String): OrderedBooks
     suspend fun getBookmark(pageNumb:Int, isbn:String):  Optional<Bookmark>
     suspend fun deleteFromBookmark(pageNumb: Int, isbn:String)
     fun getLiveOrderedBook(isbn:String): LiveData<OrderedBooks>
     suspend fun deleteAllHistory()
     suspend fun addReadHistory(history: History)
     suspend fun addUserReviews(userReviews: List<UserReview>)
     suspend fun getUserReviews(isVerified:Boolean): List<UserReview>
     suspend fun deletePaymentCard(card: PaymentCard)
     suspend fun getPaymentCards(): List<PaymentCard>
     suspend fun deleteAllCartItems()
     suspend fun deleteFromCart(isbnList: List<String>)
     suspend fun getAllPaymentTransactions(): List<PaymentTransaction>
     suspend fun addPaymentTransactions(paymentTransactions: List<PaymentTransaction>)
     suspend fun deleteAllPaymentTransactions()
     fun getLivePaymentCards(): LiveData<List<PaymentCard>>
     suspend fun deleteAllPaymentCards()
     suspend fun addPaymentCard(paymentCard: PaymentCard)
     fun getLivePublishedBook(isbn: String): LiveData<Optional<PublishedBook>>
     suspend fun getOrderedBooks(userId:String): List<OrderedBooks>
     fun getALiveOrderedBook(isbn:String): LiveData<Optional<OrderedBooks>>
     fun deleteAllOrderedBooks()
     suspend fun updateReview(isbn: String, isVerified:Boolean)
     suspend fun deleteAllReviews()
     suspend fun deleteUserRecord()
     suspend fun getUserReview(isbn:String): Optional<UserReview>
     suspend fun addUserReview(userReview: UserReview)
     fun getLiveUserReview(isbn:String): LiveData<Optional<UserReview>>
     suspend fun getPublishedBook(isbn: String): Optional<PublishedBook>
     fun getLiveBookmarks(deleted: Boolean = false): LiveData<List<Bookmark>>
     suspend fun getListOfCartItems(userId: String):List<Cart>
     fun getLiveListOfCartItems(userId: String):LiveData<List<Cart>>
     suspend fun getDeletedBookmarks(deleted: Boolean, uploaded: Boolean):List<Bookmark>
     suspend fun deleteAllBookmarks()
     suspend fun addBookmarkList(bookmarks: List<Bookmark>)
     suspend fun addToCart(cart:Cart)
     suspend fun deleteFromCart(cart: Cart)
     suspend fun addBookmark(bookmark: Bookmark)
     suspend fun deleteBookmarks(bookmarks: List<Bookmark>)
     suspend fun getLocalBookmarks(uploaded:Boolean=false, deleted: Boolean=false): List<Bookmark>
     suspend fun getBookmarks(deleted: Boolean = false):List<Bookmark>
     fun getLiveTotalCartItemsNo(userId: String): LiveData<Int>
     suspend fun getUser(userId: String): Optional<User>
     fun getLiveUser(userId: String): LiveData<User>
     suspend fun addUser(user: User)
     suspend fun getBookInterest(userId: String): Optional<BookInterest>
     fun getLiveBookInterest(userId: String): LiveData<Optional<BookInterest>>
     suspend fun addBookInterest(bookInterest: BookInterest)
     suspend fun addOrderedBooks(OrderedBooks: List<OrderedBooks>)
     fun getLiveOrderedBooks(userId: String): LiveData<List<OrderedBooks>>
     suspend fun addStoreSearchHistory(searchHistory: StoreSearchHistory)
     suspend fun addShelfSearchHistory(shelfSearchHistory: ShelfSearchHistory)
     fun getLiveShelfSearchHistory(userId: String): LiveData<List<ShelfSearchHistory>>
     fun getLiveStoreSearchHistory(userId: String): LiveData<List<StoreSearchHistory>>
     fun getLivePubReferrer(isbn: String): LiveData<Optional<PubReferrers>>
     suspend fun addPubReferrer(pubReferrers: PubReferrers)
     suspend fun updateRecommendedBooksByCategory(category: String, isRecommended: Boolean = true)
     suspend fun updateRecommendedBooksByTag(tag: String, isRecommended: Boolean = true)
     suspend fun addAllPubBooks(pubBooks: List<PublishedBook>)
     suspend fun deleteUnPublishedBookRecords(unPublishedBooks: List<PublishedBook>)
     suspend fun getPublishedBooks(): List<PublishedBook>
     fun getLiveTrendingBooks(): LiveData<List<PublishedBook>>
     fun getLiveRecommendedBooks(): LiveData<List<PublishedBook>>
     fun getLiveBooksByCategory(category: String): LiveData<List<PublishedBook>>
     fun getLivePublishedBooks(): LiveData<List<PublishedBook>>
     fun getBooksByCategoryPageSource(category: String): PagingSource<Int, PublishedBook>
     fun getTrendingBooksPageSource(): PagingSource<Int, PublishedBook>
     fun getRecommendedBooksPageSource(): PagingSource<Int, PublishedBook>
}