package com.bookshelfhub.bookshelfhub.services.database.local.room

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import com.bookshelfhub.bookshelfhub.services.database.local.ILocalDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.*
import com.bookshelfhub.bookshelfhub.workers.UnPublishedBooks
import com.google.common.base.Optional
import javax.inject.Inject

open class RoomDb @Inject constructor (private val context:Context) : ILocalDb {


    override suspend fun addUserReview(userReview: UserReview) {
        RoomInstance.getDatabase(context).userDao().addUserReview(userReview)
    }

    //TODO User Review
    override fun getLiveUserReview(userId: String): LiveData<Optional<UserReview>> {
        return  RoomInstance.getDatabase(context).userDao().getLiveUserReview(userId)
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
    override suspend fun addOrderedBook(orderedBooks: OrderedBooks){
        RoomInstance.getDatabase(context).userDao().addBookOrdered(orderedBooks)
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

    override suspend fun getPublishedBook(isbn: String): PublishedBooks {
       return RoomInstance.getDatabase(context).userDao().getPublishedBook(isbn)
    }

    override suspend fun updateRecommendedBooksByCategory(category: String, isRecommended:Boolean){
        RoomInstance.getDatabase(context).userDao().updateRecommendedBooksByCategory(category, isRecommended)
    }

    override suspend fun updateRecommendedBooksByTag(tag: String, isRecommended:Boolean){
        RoomInstance.getDatabase(context).userDao().updateRecommendedBooksByTag(tag,isRecommended)
    }

    override suspend fun addAllPubBooks(pubBooks:List<PublishedBooks>){
        RoomInstance.getDatabase(context).userDao().addAllPubBooks(pubBooks)
    }

    override suspend fun deleteUnPublishedBookRecords(unPublishedBooks : List<PublishedBooks>){
        RoomInstance.getDatabase(context).userDao().deleteUnPublishedBookRecords(unPublishedBooks)
    }

    override suspend fun getPublishedBooks(): List<PublishedBooks> {
        return RoomInstance.getDatabase(context).userDao().getPublishedBooks()
    }

    override fun getLiveTrendingBooks(): LiveData<List<PublishedBooks>> {
        return RoomInstance.getDatabase(context).userDao().getLiveTrendingBooks()
    }

    override fun getLiveRecommendedBooks(): LiveData<List<PublishedBooks>> {
        return RoomInstance.getDatabase(context).userDao().getLiveRecommendedBooks()
    }

    override fun getLiveBooksByCategory(category:String): LiveData<List<PublishedBooks>> {
        return RoomInstance.getDatabase(context).userDao().getLiveBooksByCategory(category)
    }

    override fun getLivePublishedBooks(): LiveData<List<PublishedBooks>> {
        return RoomInstance.getDatabase(context).userDao().getLivePublishedBooks()
    }

    override fun getBooksByCategoryPageSource(category:String): PagingSource<Int, PublishedBooks> {
        return RoomInstance.getDatabase(context).userDao().getBooksByCategoryPageSource(category)
    }

    override fun getTrendingBooksPageSource(): PagingSource<Int, PublishedBooks> {
        return RoomInstance.getDatabase(context).userDao().getTrendingBooksPageSource()
    }

    override fun getRecommendedBooksPageSource(): PagingSource<Int, PublishedBooks> {
       return RoomInstance.getDatabase(context).userDao().getRecommendedBooksPageSource()
    }
}

