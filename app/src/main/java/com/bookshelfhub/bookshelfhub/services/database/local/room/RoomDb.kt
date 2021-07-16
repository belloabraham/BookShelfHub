package com.bookshelfhub.bookshelfhub.services.database.local.room

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.*
import com.google.common.base.Optional

open class RoomDb(private val context:Context) {


    //Todo User
    open suspend fun getUser(userId:String): Optional<User> {
      return  RoomInstance.getDatabase(context).userDao().getUser(userId)
    }

    open fun getLiveUser(userId:String): LiveData<User> {
        return  RoomInstance.getDatabase(context).userDao().getLiveUser(userId)
    }

    open suspend fun addUser(user:User){
        RoomInstance.getDatabase(context).userDao().addUser(user)
    }

    //Todo Book Interest
    open suspend fun getBookInterest(userId:String): Optional<BookInterest> {
        return  RoomInstance.getDatabase(context).userDao().getBookInterest(userId)
    }

    open fun getLiveBookInterest(userId:String): LiveData<Optional<BookInterest>> {
        return  RoomInstance.getDatabase(context).userDao().getLiveBookInterest(userId)
    }

    suspend fun addBookInterest(bookInterest: BookInterest){
        RoomInstance.getDatabase(context).userDao().addBookInterest(bookInterest)
    }

    //Todo Ordered Books
    open suspend fun addOrderedBook(orderedBooks: OrderedBooks){
        RoomInstance.getDatabase(context).userDao().addBookOrdered(orderedBooks)
    }
    open fun getLiveOrderedBooks(userId: String): LiveData<List<OrderedBooks>> {
       return  RoomInstance.getDatabase(context).userDao().getLiveBooksOrdered(userId)
    }


    //TODO Payment Info
    open suspend fun addPaymentInfo(paymentInfo: PaymentInfo){
        RoomInstance.getDatabase(context).userDao().addPaymentInfo(paymentInfo)
    }

    //TODO Search History
    open suspend fun addStoreSearchHistory(searchHistory: StoreSearchHistory){
        RoomInstance.getDatabase(context).userDao().addStoreSearchHistory(searchHistory)
    }

    open suspend fun addShelfSearchHistory(shelfSearchHistory: ShelfSearchHistory){
        RoomInstance.getDatabase(context).userDao().addShelfSearchHistory(shelfSearchHistory)
    }

    open fun getLiveShelfSearchHistory(userId:String):LiveData<List<ShelfSearchHistory>> {
      return  RoomInstance.getDatabase(context).userDao().getLiveShelfSearchHistory(userId)
    }

    open fun getLiveStoreSearchHistory(userId:String):LiveData<List<StoreSearchHistory>>{
       return RoomInstance.getDatabase(context).userDao().getLiveStoreSearchHistory(userId)
    }

    //TODO Referrer
    open suspend fun getPubReferrer(isbn:String): Optional<PubReferrers> {
        return  RoomInstance.getDatabase(context).userDao().getPubReferrer(isbn)
    }

    open suspend fun addPubReferrer(pubReferrers: PubReferrers){
        RoomInstance.getDatabase(context).userDao().addPubReferrer(pubReferrers)
    }

    //TODO Published Books
    open suspend fun addAllPubBooks(pubBooks:List<PublishedBooks>){
        RoomInstance.getDatabase(context).userDao().addAllPubBooks(pubBooks)
    }

    open suspend fun deletePublishedBookRecords(isbnList:List<String>){
        RoomInstance.getDatabase(context).userDao().deletePublishedBookRecords(isbnList)
    }

    open suspend fun getPublishedBooks(): List<PublishedBooks> {
        return RoomInstance.getDatabase(context).userDao().getPublishedBooks()
    }

    open fun getLiveBooksByCategory(category:String): LiveData<List<PublishedBooks>> {
        return RoomInstance.getDatabase(context).userDao().getLiveBooksByCategory(category)
    }

    open fun getLivePublishedBooks(): LiveData<List<PublishedBooks>> {
        return RoomInstance.getDatabase(context).userDao().getLivePublishedBooks()
    }

    open fun getBooksByCategoryPageSource(category:String): PagingSource<Int, PublishedBooks> {
        return RoomInstance.getDatabase(context).userDao().getBooksByCategoryPageSource(category)
    }

    open fun getTrendingBooksPageSource(trending:Boolean=true): PagingSource<Int, PublishedBooks> {
        return RoomInstance.getDatabase(context).userDao().getTrendingBooksPageSource(trending)
    }

    open fun getRecommendedBooksPageSource(recommended:Boolean=true): PagingSource<Int, PublishedBooks> {
       return RoomInstance.getDatabase(context).userDao().getRecommendedBooksPageSource(recommended)
    }
}

