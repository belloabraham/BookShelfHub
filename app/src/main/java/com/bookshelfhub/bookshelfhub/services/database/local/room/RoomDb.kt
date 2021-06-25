package com.bookshelfhub.bookshelfhub.services.database.local.room

import android.content.Context
import androidx.lifecycle.LiveData
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.BookInterestRecord
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.BooksOrderedRecord
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.PaymentInfoRecord
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.UserRecord
import com.google.common.base.Optional

open class RoomDb(private val context:Context) {

    open fun getUser(userId:String): Optional<UserRecord> {
      return  RoomInstance.getDatabase(context).userDao().getUser(userId)
    }

    open fun getBookInterest(userId:String): Optional<BookInterestRecord> {
        return  RoomInstance.getDatabase(context).userDao().getBookInterest(userId)
    }

    open fun getLiveUser(userId:String): LiveData<UserRecord> {
        return  RoomInstance.getDatabase(context).userDao().getLiveUser(userId)
    }

    open suspend fun addUser(userRecord:UserRecord){
        RoomInstance.getDatabase(context).userDao().addUser(userRecord)
    }

    suspend fun addBookInterest(bookInterest: BookInterestRecord){
        RoomInstance.getDatabase(context).userDao().addBookInterest(bookInterest)
    }

    open suspend fun addBookOrdered(booksOrderedRecord: BooksOrderedRecord){
        RoomInstance.getDatabase(context).userDao().addBookOrdered(booksOrderedRecord)
    }

    open suspend fun addPaymentInfo(paymentInfoRecord: PaymentInfoRecord){
        RoomInstance.getDatabase(context).userDao().addPaymentInfo(paymentInfoRecord)
    }

}