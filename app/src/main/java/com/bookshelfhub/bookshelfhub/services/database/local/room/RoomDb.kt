package com.bookshelfhub.bookshelfhub.services.database.local.room

import android.content.Context
import androidx.lifecycle.LiveData
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.*
import com.google.common.base.Optional

open class RoomDb(private val context:Context) {


    //Todo User
    open fun getUser(userId:String): Optional<UserRecord> {
      return  RoomInstance.getDatabase(context).userDao().getUser(userId)
    }

    open fun getLiveUser(userId:String): LiveData<UserRecord> {
        return  RoomInstance.getDatabase(context).userDao().getLiveUser(userId)
    }

    open suspend fun addUser(userRecord:UserRecord){
        RoomInstance.getDatabase(context).userDao().addUser(userRecord)
    }

    //Todo Book Interest
    open fun getBookInterest(userId:String): Optional<BookInterestRecord> {
        return  RoomInstance.getDatabase(context).userDao().getBookInterest(userId)
    }

    open fun getLiveBookInterest(userId:String): LiveData<Optional<BookInterestRecord>> {
        return  RoomInstance.getDatabase(context).userDao().getLiveBookInterest(userId)
    }

    suspend fun addBookInterest(bookInterest: BookInterestRecord){
        RoomInstance.getDatabase(context).userDao().addBookInterest(bookInterest)
    }

    //Todo Ordered Books
    open suspend fun addBookOrdered(booksOrderedRecord: BooksOrderedRecord){
        RoomInstance.getDatabase(context).userDao().addBookOrdered(booksOrderedRecord)
    }

    //TODO Payment Info
    open suspend fun addPaymentInfo(paymentInfoRecord: PaymentInfoRecord){
        RoomInstance.getDatabase(context).userDao().addPaymentInfo(paymentInfoRecord)
    }

    //TODO Search History
    open suspend fun addStoreSearchHistory(searchHistory: StoreSearchHistory){
        RoomInstance.getDatabase(context).userDao().addStoreSearchHistory(searchHistory)
    }

    open suspend fun addShelfSearchHistory(searchHistory: ShelfSearchHistory){
        RoomInstance.getDatabase(context).userDao().addShelfSearchHistory(searchHistory)
    }

    open fun getShelfSearchHistory(userId:String):LiveData<Optional<List<ShelfSearchHistory>>> {
      return  RoomInstance.getDatabase(context).userDao().getShelfSearchHistory(userId)
    }

    open fun getStoreSearchHistory(userId:String):LiveData<Optional<List<StoreSearchHistory>>>{
       return RoomInstance.getDatabase(context).userDao().getStoreSearchHistory(userId)
    }

}