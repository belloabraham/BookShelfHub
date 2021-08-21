package com.bookshelfhub.bookshelfhub.services.database.local.room

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import com.bookshelfhub.bookshelfhub.services.database.local.ILocalDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.*
import com.google.common.base.Optional
import javax.inject.Inject

open class RoomDb @Inject constructor (private val context:Context) : ILocalDb {


    override suspend fun deleteUserRecord() {
        return  RoomInstance.getDatabase(context).userDao().deleteUserRecord()
    }

    //TODO User Review
    override fun getLiveUserReview(isbn: String): LiveData<Optional<UserReview>> {
        return  RoomInstance.getDatabase(context).userDao().getLiveUserReview(isbn)
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
    override suspend fun getBookmarks(userId: String, deleted: Boolean): List<Bookmark> {
        return  RoomInstance.getDatabase(context).userDao().getBookmarks(userId, deleted)
    }

    override suspend fun addBookmark(bookmark: Bookmark) {
        RoomInstance.getDatabase(context).userDao().addBookmark(bookmark)
    }

    override suspend fun getDeletedBookmarks(userId: String, deleted: Boolean): List<Bookmark> {
       return RoomInstance.getDatabase(context).userDao().getDeletedBookmarks(userId, deleted)
    }

    override suspend fun addBookmarkList(bookmarks: List<Bookmark>) {
        RoomInstance.getDatabase(context).userDao().addBookmarkList(bookmarks)
    }

    override suspend fun deleteBookmarks(bookmarks: List<Bookmark>) {
        RoomInstance.getDatabase(context).userDao().deleteBookmarks(bookmarks)
    }

    override suspend fun getLocalBookmarks(
        userId: String,
        uploaded: Boolean,
        deleted: Boolean
    ): List<Bookmark> {
        return  RoomInstance.getDatabase(context).userDao().getLocalBookmarks(userId, uploaded, deleted)
    }

    override fun getLiveBookmarks(userId: String, deleted: Boolean): LiveData<List<Bookmark>> {
        return  RoomInstance.getDatabase(context).userDao().getLiveBookmarks(userId, deleted)
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

    override suspend fun deleteFromCart(cart: Cart) {
        RoomInstance.getDatabase(context).userDao().deleteFromCart(cart)
    }


    //Todo User
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

    override suspend fun getOrderedBooks(userId: String): List<OrderedBooks> {
        return RoomInstance.getDatabase(context).userDao().getOrderedBooks(userId)
    }

    override fun getAnOrderedBook(isbn: String, userId: String): Optional<OrderedBooks> {
       return RoomInstance.getDatabase(context).userDao().getAnOrderedBook(isbn, userId)
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
    override suspend fun getPubReferrer(isbn:String): Optional<PubReferrers> {
        return  RoomInstance.getDatabase(context).userDao().getPubReferrer(isbn)
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

