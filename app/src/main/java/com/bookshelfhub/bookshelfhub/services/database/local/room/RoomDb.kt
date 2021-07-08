package com.bookshelfhub.bookshelfhub.services.database.local.room

import android.content.Context
import androidx.lifecycle.LiveData
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.*
import com.google.common.base.Optional

open class RoomDb(private val context:Context) {


    //Todo User
    open fun getUser(userId:String): Optional<User> {
      return  RoomInstance.getDatabase(context).userDao().getUser(userId)
    }

    open fun getLiveUser(userId:String): LiveData<User> {
        return  RoomInstance.getDatabase(context).userDao().getLiveUser(userId)
    }

    open suspend fun addUser(user:User){
        RoomInstance.getDatabase(context).userDao().addUser(user)
    }

    //Todo Book Interest
    open fun getBookInterest(userId:String): Optional<BookInterest> {
        return  RoomInstance.getDatabase(context).userDao().getBookInterest(userId)
    }

    open fun getLiveBookInterest(userId:String): LiveData<Optional<BookInterest>> {
        return  RoomInstance.getDatabase(context).userDao().getLiveBookInterest(userId)
    }

    suspend fun addBookInterest(bookInterest: BookInterest){
        RoomInstance.getDatabase(context).userDao().addBookInterest(bookInterest)
    }

    //Todo Ordered Books
    open suspend fun addBookOrdered(orderedBooks: OrderedBooks){
        RoomInstance.getDatabase(context).userDao().addBookOrdered(orderedBooks)
    }

    //TODO Payment Info
    open suspend fun addPaymentInfo(paymentInfo: PaymentInfo){
        RoomInstance.getDatabase(context).userDao().addPaymentInfo(paymentInfo)
    }

    //TODO Search History
    open suspend fun addStoreSearchHistory(searchHistory: StoreSearchHistory){
        RoomInstance.getDatabase(context).userDao().addStoreSearchHistory(searchHistory)
    }

    open suspend fun addShelfSearchHistory(searchHistory: ShelfSearchHistory){
        RoomInstance.getDatabase(context).userDao().addShelfSearchHistory(searchHistory)
    }

    open fun getShelfSearchHistory(userId:String):LiveData<List<ShelfSearchHistory>> {
      return  RoomInstance.getDatabase(context).userDao().getShelfSearchHistory(userId)
    }

    open fun getStoreSearchHistory(userId:String):LiveData<List<StoreSearchHistory>>{
       return RoomInstance.getDatabase(context).userDao().getStoreSearchHistory(userId)
    }

    //TODO Referrer
    open fun getPubReferrer(isbn:String): Optional<PubReferrers> {
        return  RoomInstance.getDatabase(context).userDao().getPubReferrer(isbn)
    }

    open suspend fun addPubReferrer(pubReferrers: PubReferrers){
        RoomInstance.getDatabase(context).userDao().addPubReferrer(pubReferrers)
    }
}