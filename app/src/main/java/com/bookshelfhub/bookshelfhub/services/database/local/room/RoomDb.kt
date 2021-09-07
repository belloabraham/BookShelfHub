package com.bookshelfhub.bookshelfhub.services.database.local.room

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import com.bookshelfhub.bookshelfhub.services.database.local.ILocalDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.*
import com.google.common.base.Optional
import javax.inject.Inject

open class RoomDb @Inject constructor (private val context:Context) : ILocalDb {


    //TODO Read History
    override suspend fun addReadHistory(history: History) {
        RoomInstance.getDatabase(context).userDao().addReadHistory(history)
    }

    override suspend fun deleteAllHistory() {
        RoomInstance.getDatabase(context).userDao().deleteAllHistory()
    }

    //TODO Payment transactions
    override suspend fun addPaymentTransactions(paymentTransactions: List<PaymentTransaction>) {
        RoomInstance.getDatabase(context).userDao().addPaymentTransactions(paymentTransactions)
    }

    override suspend fun getAllPaymentTransactions(): List<PaymentTransaction> {
       return RoomInstance.getDatabase(context).userDao().getAllPaymentTransactions()
    }

    override suspend fun deleteAllPaymentTransactions() {
        RoomInstance.getDatabase(context).userDao().deleteAllPaymentTransactions()
    }

    //TODO Payment card
    override suspend fun addPaymentCard(paymentCard: PaymentCard) {
        RoomInstance.getDatabase(context).userDao().addPaymentCard(paymentCard)
    }

    override suspend fun getUserReviews(isVerified: Boolean): List<UserReview> {
      return  RoomInstance.getDatabase(context).userDao().getUserReviews(isVerified)
    }

    override suspend fun deletePaymentCard(card: PaymentCard) {
        RoomInstance.getDatabase(context).userDao().deletePaymentCard(card)
    }


    override suspend fun getPaymentCards(): List<PaymentCard> {
      return  RoomInstance.getDatabase(context).userDao().getPaymentCards()
    }

    override fun getLivePaymentCards(): LiveData<List<PaymentCard>> {
       return RoomInstance.getDatabase(context).userDao().getLivePaymentCards()
    }

    override suspend fun deleteAllPaymentCards() {
        RoomInstance.getDatabase(context).userDao().deleteAllPaymentCards()
    }

    //TODO User Review
    override fun getLiveUserReview(isbn: String): LiveData<Optional<UserReview>> {
        return  RoomInstance.getDatabase(context).userDao().getLiveUserReview(isbn)
    }

    override suspend fun addUserReviews(userReviews: List<UserReview>) {
        RoomInstance.getDatabase(context).userDao().addUserReviews(userReviews)
    }


    override suspend fun updateReview(isbn: String, isVerified: Boolean) {
        return  RoomInstance.getDatabase(context).userDao().updateReview(isbn, isVerified)
    }

    override suspend fun deleteAllReviews() {
        return  RoomInstance.getDatabase(context).userDao().deleteAllReviews()
    }

    override suspend fun getUserReview(isbn: String): Optional<UserReview> {
        return RoomInstance.getDatabase(context).userDao().getUserReview(isbn)
    }

    override suspend fun addUserReview(userReview: UserReview) {
        RoomInstance.getDatabase(context).userDao().addUserReview(userReview)
    }

    //Todo Bookmarks
    override suspend fun getBookmarks(deleted: Boolean): List<Bookmark> {
        return  RoomInstance.getDatabase(context).userDao().getBookmarks(deleted)
    }

    override suspend fun addBookmark(bookmark: Bookmark) {
        RoomInstance.getDatabase(context).userDao().addBookmark(bookmark)
    }

    override suspend fun getDeletedBookmarks(deleted: Boolean, uploaded: Boolean): List<Bookmark> {
       return RoomInstance.getDatabase(context).userDao().getDeletedBookmarks(deleted, uploaded)
    }

    override suspend fun deleteAllBookmarks() {
        return RoomInstance.getDatabase(context).userDao().deleteAllBookmarks()
    }

    override suspend fun addBookmarkList(bookmarks: List<Bookmark>) {
        RoomInstance.getDatabase(context).userDao().addBookmarkList(bookmarks)
    }

    override suspend fun deleteBookmarks(bookmarks: List<Bookmark>) {
        RoomInstance.getDatabase(context).userDao().deleteBookmarks(bookmarks)
    }

    override suspend fun getLocalBookmarks(
        uploaded: Boolean,
        deleted: Boolean
    ): List<Bookmark> {
        return  RoomInstance.getDatabase(context).userDao().getLocalBookmarks(uploaded, deleted)
    }

    override fun getLiveBookmarks(deleted: Boolean): LiveData<List<Bookmark>> {
        return  RoomInstance.getDatabase(context).userDao().getLiveBookmarks( deleted)
    }

    //Todo Cart
    override suspend fun getListOfCartItems(userId: String): List<Cart> {
        return  RoomInstance.getDatabase(context).userDao().getListOfCartItems(userId)
    }

    override fun getLiveListOfCartItems(userId: String): LiveData<List<Cart>> {
       return  RoomInstance.getDatabase(context).userDao().getLiveListOfCartItems(userId)
    }

    override fun getLiveTotalCartItemsNo(userId: String): LiveData<Int> {
        return  RoomInstance.getDatabase(context).userDao().getLiveTotalCartItemsNo(userId)
    }

    override suspend fun addToCart(cart: Cart) {
        RoomInstance.getDatabase(context).userDao().addToCart(cart)
    }

    override suspend fun deleteAllCartItems() {
        RoomInstance.getDatabase(context).userDao().deleteAllCartItems()
    }

    override suspend fun deleteFromCart(isbnList: List<String>) {
        RoomInstance.getDatabase(context).userDao().deleteFromCart(isbnList)
    }

    override suspend fun deleteFromCart(cart: Cart) {
        RoomInstance.getDatabase(context).userDao().deleteFromCart(cart)
    }


    //Todo User

    override suspend fun deleteUserRecord() {
        return  RoomInstance.getDatabase(context).userDao().deleteUserRecord()
    }

    override suspend fun getUser(userId:String): Optional<User> {
      return  RoomInstance.getDatabase(context).userDao().getUser(userId)
    }

    override fun getLiveUser(userId:String): LiveData<User> {
        return  RoomInstance.getDatabase(context).userDao().getLiveUser(userId)
    }

    override suspend fun addUser(user:User){
        RoomInstance.getDatabase(context).userDao().addUser(user)
    }

    //Todo Book Interest
    override suspend fun getBookInterest(userId:String): Optional<BookInterest> {
        return  RoomInstance.getDatabase(context).userDao().getBookInterest(userId)
    }

    override fun getLiveBookInterest(userId:String): LiveData<Optional<BookInterest>> {
        return  RoomInstance.getDatabase(context).userDao().getLiveBookInterest(userId)
    }

    override suspend fun addBookInterest(bookInterest: BookInterest){
        RoomInstance.getDatabase(context).userDao().addBookInterest(bookInterest)
    }

    //Todo Ordered Books

    override fun getLiveOrderedBook(isbn: String): LiveData<OrderedBooks> {
        return RoomInstance.getDatabase(context).userDao().getLiveOrderedBook(isbn)
    }

    override suspend fun getOrderedBooks(userId: String): List<OrderedBooks> {
        return RoomInstance.getDatabase(context).userDao().getOrderedBooks(userId)
    }

    override fun getALiveOrderedBook(isbn: String): LiveData<Optional<OrderedBooks>> {
       return RoomInstance.getDatabase(context).userDao().getALiveOrderedBook(isbn)
    }

    override fun deleteAllOrderedBooks() {
        RoomInstance.getDatabase(context).userDao().deleteAllOrderedBooks()
    }

    override suspend fun addOrderedBooks(OrderedBooks: List<OrderedBooks>){
        RoomInstance.getDatabase(context).userDao().addOrderedBooks(OrderedBooks)
    }
    override fun getLiveOrderedBooks(userId: String): LiveData<List<OrderedBooks>> {
       return  RoomInstance.getDatabase(context).userDao().getLiveBooksOrdered(userId)
    }

    //TODO Search History
    override suspend fun addStoreSearchHistory(searchHistory: StoreSearchHistory){
        RoomInstance.getDatabase(context).userDao().addStoreSearchHistory(searchHistory)
    }

    override suspend fun addShelfSearchHistory(shelfSearchHistory: ShelfSearchHistory){
        RoomInstance.getDatabase(context).userDao().addShelfSearchHistory(shelfSearchHistory)
    }

    override fun getLiveShelfSearchHistory(userId:String):LiveData<List<ShelfSearchHistory>> {
      return  RoomInstance.getDatabase(context).userDao().getLiveShelfSearchHistory(userId)
    }

    override fun getLiveStoreSearchHistory(userId:String):LiveData<List<StoreSearchHistory>>{
       return RoomInstance.getDatabase(context).userDao().getLiveStoreSearchHistory(userId)
    }

    //TODO Referrer
    override fun getLivePubReferrer(isbn:String): LiveData<Optional<PubReferrers>> {
        return  RoomInstance.getDatabase(context).userDao().getLivePubReferrer(isbn)
    }

    override suspend fun addPubReferrer(pubReferrers: PubReferrers){
        RoomInstance.getDatabase(context).userDao().addPubReferrer(pubReferrers)
    }

    //TODO Published Books

    override fun getLivePublishedBook(isbn: String): LiveData<PublishedBook> {
        return RoomInstance.getDatabase(context).userDao().getLivePublishedBook(isbn)
    }

    override suspend fun getPublishedBook(isbn: String): PublishedBook {
       return RoomInstance.getDatabase(context).userDao().getPublishedBook(isbn)
    }

    override suspend fun updateRecommendedBooksByCategory(category: String, isRecommended:Boolean){
        RoomInstance.getDatabase(context).userDao().updateRecommendedBooksByCategory(category, isRecommended)
    }

    override suspend fun updateRecommendedBooksByTag(tag: String, isRecommended:Boolean){
        RoomInstance.getDatabase(context).userDao().updateRecommendedBooksByTag(tag,isRecommended)
    }

    override suspend fun addAllPubBooks(pubBooks:List<PublishedBook>){
        RoomInstance.getDatabase(context).userDao().addAllPubBooks(pubBooks)
    }

    override suspend fun deleteUnPublishedBookRecords(unPublishedBooks : List<PublishedBook>){
        RoomInstance.getDatabase(context).userDao().deleteUnPublishedBookRecords(unPublishedBooks)
    }

    override suspend fun getPublishedBooks(): List<PublishedBook> {
        return RoomInstance.getDatabase(context).userDao().getPublishedBooks()
    }

    override fun getLiveTrendingBooks(): LiveData<List<PublishedBook>> {
        return RoomInstance.getDatabase(context).userDao().getLiveTrendingBooks()
    }

    override fun getLiveRecommendedBooks(): LiveData<List<PublishedBook>> {
        return RoomInstance.getDatabase(context).userDao().getLiveRecommendedBooks()
    }

    override fun getLiveBooksByCategory(category:String): LiveData<List<PublishedBook>> {
        return RoomInstance.getDatabase(context).userDao().getLiveBooksByCategory(category)
    }

    override fun getLivePublishedBooks(): LiveData<List<PublishedBook>> {
        return RoomInstance.getDatabase(context).userDao().getLivePublishedBooks()
    }

    override fun getBooksByCategoryPageSource(category:String): PagingSource<Int, PublishedBook> {
        return RoomInstance.getDatabase(context).userDao().getBooksByCategoryPageSource(category)
    }

    override fun getTrendingBooksPageSource(): PagingSource<Int, PublishedBook> {
        return RoomInstance.getDatabase(context).userDao().getTrendingBooksPageSource()
    }

    override fun getRecommendedBooksPageSource(): PagingSource<Int, PublishedBook> {
       return RoomInstance.getDatabase(context).userDao().getRecommendedBooksPageSource()
    }
}

