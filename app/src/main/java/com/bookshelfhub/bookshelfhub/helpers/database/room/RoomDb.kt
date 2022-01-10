package com.bookshelfhub.bookshelfhub.helpers.database.room

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import com.bookshelfhub.bookshelfhub.helpers.database.ILocalDb
import com.bookshelfhub.bookshelfhub.helpers.database.room.entities.*
import com.google.common.base.Optional
import javax.inject.Inject

open class RoomDb @Inject constructor (private val userDao: UserDao) : ILocalDb {


    //TODO Payment card
    override suspend fun addPaymentCard(paymentCard: PaymentCard) {
        userDao.addPaymentCard(paymentCard)
    }

    override suspend fun deletePaymentCard(card: PaymentCard) {
        userDao.deletePaymentCard(card)
    }


    override suspend fun getPaymentCards(): List<PaymentCard> {
        return  userDao.getPaymentCards()
    }

    override fun getLivePaymentCards(): LiveData<List<PaymentCard>> {
        return userDao.getLivePaymentCards()
    }

    override suspend fun deleteAllPaymentCards() {
        userDao.deleteAllPaymentCards()
    }


    //TODO Book Videos
    override fun getLiveListOfBookVideos(isbn: String): LiveData<List<BookVideos>> {
        return  userDao.getLiveListOfBookVideos(isbn)
    }

    //Todo Book Videos
    override suspend fun addBookVideos(bookVideos: List<BookVideos>) {
        userDao.addBookVideos(bookVideos)
    }


    //TODO Read History
    override suspend fun addReadHistory(history: History) {
        userDao.addReadHistory(history)
    }

    override suspend fun deleteAllHistory() {
        userDao.deleteAllHistory()
    }

    //TODO Payment transactions
    override suspend fun addPaymentTransactions(paymentTransactions: List<PaymentTransaction>) {
        userDao.addPaymentTransactions(paymentTransactions)
    }

    override suspend fun getAllPaymentTransactions(): List<PaymentTransaction> {
       return userDao.getAllPaymentTransactions()
    }

    override suspend fun deleteAllPaymentTransactions() {
        userDao.deleteAllPaymentTransactions()
    }

    override suspend fun getUserReviews(isVerified: Boolean): List<UserReview> {
      return  userDao.getUserReviews(isVerified)
    }


    //TODO User Review
    override fun getLiveUserReview(isbn: String): LiveData<Optional<UserReview>> {
        return  userDao.getLiveUserReview(isbn)
    }

    override suspend fun addUserReviews(userReviews: List<UserReview>) {
        userDao.addUserReviews(userReviews)
    }


    override suspend fun updateReview(isbn: String, isVerified: Boolean) {
        return  userDao.updateReview(isbn, isVerified)
    }

    override suspend fun deleteAllReviews() {
        return  userDao.deleteAllReviews()
    }

    override suspend fun getUserReview(isbn: String): Optional<UserReview> {
        return userDao.getUserReview(isbn)
    }

    override suspend fun addUserReview(userReview: UserReview) {
        userDao.addUserReview(userReview)
    }

    //Todo Bookmarks
    override suspend fun getBookmarks(deleted: Boolean): List<Bookmark> {
        return  userDao.getBookmarks(deleted)
    }

    override suspend fun getBookmark(pageNumb: Int, isbn: String): Optional<Bookmark> {
        return  userDao.getBookmark(pageNumb, isbn)
    }

    override suspend fun deleteFromBookmark(pageNumb: Int, isbn:String) {
        userDao.deleteFromBookmark(pageNumb, isbn)
    }

    override suspend fun addBookmark(bookmark: Bookmark) {
        userDao.addBookmark(bookmark)
    }

    override suspend fun getDeletedBookmarks(deleted: Boolean, uploaded: Boolean): List<Bookmark> {
       return userDao.getDeletedBookmarks(deleted, uploaded)
    }

    override suspend fun deleteAllBookmarks() {
        return userDao.deleteAllBookmarks()
    }

    override suspend fun addBookmarkList(bookmarks: List<Bookmark>) {
        userDao.addBookmarkList(bookmarks)
    }

    override suspend fun deleteBookmarks(bookmarks: List<Bookmark>) {
        userDao.deleteBookmarks(bookmarks)
    }

    override suspend fun getLocalBookmarks(
        uploaded: Boolean,
        deleted: Boolean
    ): List<Bookmark> {
        return  userDao.getLocalBookmarks(uploaded, deleted)
    }

    override fun getLiveBookmarks(deleted: Boolean): LiveData<List<Bookmark>> {
        return  userDao.getLiveBookmarks( deleted)
    }

    //Todo Cart
    override suspend fun getListOfCartItems(userId: String): List<Cart> {
        return  userDao.getListOfCartItems(userId)
    }

    override fun getLiveListOfCartItems(userId: String): LiveData<List<Cart>> {
       return  userDao.getLiveListOfCartItems(userId)
    }

    override fun getLiveTotalCartItemsNo(userId: String): LiveData<Int> {
        return  userDao.getLiveTotalCartItemsNo(userId)
    }

    override suspend fun addToCart(cart: Cart) {
        userDao.addToCart(cart)
    }

    override suspend fun deleteAllCartItems() {
        userDao.deleteAllCartItems()
    }

    override suspend fun deleteFromCart(isbnList: List<String>) {
        userDao.deleteFromCart(isbnList)
    }

    override suspend fun deleteFromCart(cart: Cart) {
        userDao.deleteFromCart(cart)
    }


    //Todo User

    override suspend fun deleteUserRecord() {
        return  userDao.deleteUserRecord()
    }

    override suspend fun getUser(userId:String): Optional<User> {
      return  userDao.getUser(userId)
    }

    override fun getLiveUser(userId:String): LiveData<User> {
        return  userDao.getLiveUser(userId)
    }

    override suspend fun addUser(user:User){
        userDao.addUser(user)
    }

    //Todo Book Interest
    override suspend fun getBookInterest(userId:String): Optional<BookInterest> {
        return  userDao.getBookInterest(userId)
    }

    override fun getLiveBookInterest(userId:String): LiveData<Optional<BookInterest>> {
        return  userDao.getLiveBookInterest(userId)
    }

    override suspend fun addBookInterest(bookInterest: BookInterest){
        userDao.addBookInterest(bookInterest)
    }

    //Todo Ordered Books

    override suspend fun getAnOrderedBook(isbn: String): OrderedBooks {
        return  userDao.getAnOrderedBook(isbn)
    }

    override fun getLiveOrderedBook(isbn: String): LiveData<OrderedBooks> {
        return userDao.getLiveOrderedBook(isbn)
    }

    override suspend fun getOrderedBooks(userId: String): List<OrderedBooks> {
        return userDao.getOrderedBooks(userId)
    }

    override fun getALiveOrderedBook(isbn: String): LiveData<Optional<OrderedBooks>> {
       return userDao.getALiveOrderedBook(isbn)
    }

    override fun deleteAllOrderedBooks() {
        userDao.deleteAllOrderedBooks()
    }

    override suspend fun addOrderedBooks(OrderedBooks: List<OrderedBooks>){
        userDao.addOrderedBooks(OrderedBooks)
    }
    override fun getLiveOrderedBooks(userId: String): LiveData<List<OrderedBooks>> {
       return  userDao.getLiveBooksOrdered(userId)
    }

    //TODO Search History
    override suspend fun addStoreSearchHistory(searchHistory: StoreSearchHistory){
        userDao.addStoreSearchHistory(searchHistory)
    }

    override suspend fun addShelfSearchHistory(shelfSearchHistory: ShelfSearchHistory){
        userDao.addShelfSearchHistory(shelfSearchHistory)
    }

    override fun getLiveShelfSearchHistory(userId:String):LiveData<List<ShelfSearchHistory>> {
      return  userDao.getLiveShelfSearchHistory(userId)
    }

    override fun getLiveStoreSearchHistory(userId:String):LiveData<List<StoreSearchHistory>>{
       return userDao.getLiveStoreSearchHistory(userId)
    }

    //TODO Referrer
    override fun getLivePubReferrer(isbn:String): LiveData<Optional<PubReferrers>> {
        return  userDao.getLivePubReferrer(isbn)
    }

    override suspend fun addPubReferrer(pubReferrers: PubReferrers){
        userDao.addPubReferrer(pubReferrers)
    }

    //TODO Published Books

    override fun getLivePublishedBook(isbn: String): LiveData<Optional<PublishedBook>> {
        return userDao.getLivePublishedBook(isbn)
    }

    override suspend fun getPublishedBook(isbn: String): Optional<PublishedBook> {
       return userDao.getPublishedBook(isbn)
    }

    override suspend fun updateRecommendedBooksByCategory(category: String, isRecommended:Boolean){
        userDao.updateRecommendedBooksByCategory(category, isRecommended)
    }

    override suspend fun updateRecommendedBooksByTag(tag: String, isRecommended:Boolean){
        userDao.updateRecommendedBooksByTag(tag,isRecommended)
    }

    override suspend fun addAllPubBooks(pubBooks:List<PublishedBook>){
        userDao.addAllPubBooks(pubBooks)
    }

    override suspend fun deleteUnPublishedBookRecords(unPublishedBooks : List<PublishedBook>){
        userDao.deleteUnPublishedBookRecords(unPublishedBooks)
    }

    override suspend fun getPublishedBooks(): List<PublishedBook> {
        return userDao.getPublishedBooks()
    }

    override fun getLiveTrendingBooks(): LiveData<List<PublishedBook>> {
        return userDao.getLiveTrendingBooks()
    }

    override fun getLiveRecommendedBooks(): LiveData<List<PublishedBook>> {
        return userDao.getLiveRecommendedBooks()
    }

    override fun getLiveBooksByCategory(category:String): LiveData<List<PublishedBook>> {
        return userDao.getLiveBooksByCategory(category)
    }

    override fun getLivePublishedBooks(): LiveData<List<PublishedBook>> {
        return userDao.getLivePublishedBooks()
    }

    override fun getBooksByCategoryPageSource(category:String): PagingSource<Int, PublishedBook> {
        return userDao.getBooksByCategoryPageSource(category)
    }

    override fun getTrendingBooksPageSource(): PagingSource<Int, PublishedBook> {
        return userDao.getTrendingBooksPageSource()
    }

    override fun getRecommendedBooksPageSource(): PagingSource<Int, PublishedBook> {
       return userDao.getRecommendedBooksPageSource()
    }
}

