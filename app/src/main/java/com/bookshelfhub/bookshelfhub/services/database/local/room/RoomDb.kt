package com.bookshelfhub.bookshelfhub.services.database.local.room

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import com.bookshelfhub.bookshelfhub.services.database.local.ILocalDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.*
import com.google.common.base.Optional
import javax.inject.Inject

open class RoomDb @Inject constructor (private val context:Context) : ILocalDb {


    //Todo Bookmarks
    override fun getLiveBookmarks(): LiveData<List<Bookmarks>> {
        return  RoomInstance.getDatabase(context).userDao().getLiveBookmarks()
    }

    override suspend fun updateLocalBookmarks(uploaded:Boolean, idList:List<Long>) {
        return  RoomInstance.getDatabase(context).userDao().updateLocalBookmarks(uploaded,idList)
    }

    override suspend fun getLocalBookmarks(uploaded:Boolean): List<Bookmarks> {
        return  RoomInstance.getDatabase(context).userDao().getLocalBookmarks(uploaded)
    }

    //Todo Cart
    override fun getLiveTotalCartItemsNo(): LiveData<Int> {
        return  RoomInstance.getDatabase(context).userDao().getLiveTotalCartItemsNo()
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


    //TODO Payment Info
    override suspend fun addPaymentInfo(paymentInfo: PaymentInfo){
        RoomInstance.getDatabase(context).userDao().addPaymentInfo(paymentInfo)
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


    override suspend fun updateRecommendedBooksByCategory(category: String, isRecommended:Boolean){
        RoomInstance.getDatabase(context).userDao().updateRecommendedBooksByCategory(category, isRecommended)
    }

    override suspend fun updateRecommendedBooksByTag(tag: String, isRecommended:Boolean){
        RoomInstance.getDatabase(context).userDao().updateRecommendedBooksByTag(tag,isRecommended)
    }

    override suspend fun addAllPubBooks(pubBooks:List<PublishedBooks>){
        RoomInstance.getDatabase(context).userDao().addAllPubBooks(pubBooks)
    }

    override suspend fun deleteUnPublishedBookRecords(isbnList:List<String>){
        RoomInstance.getDatabase(context).userDao().deleteUnPublishedBookRecords(isbnList)
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

