package com.bookshelfhub.bookshelfhub.services.database.local.room

import android.content.Context
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.BooksOrderedRecord
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.PaymentInfoRecord
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.UserRecord
import com.google.common.base.Optional

open class RoomDb(private val context:Context) {

    open fun getUser(): Optional<UserRecord> {
      return  RoomInstance.getDatabase(context).userDao().getUser()
    }

    open suspend fun addUser(userRecord:UserRecord){
        RoomInstance.getDatabase(context).userDao().addUser(userRecord)
    }

    open suspend fun addBookOrdered(booksOrderedRecord: BooksOrderedRecord){
        RoomInstance.getDatabase(context).userDao().addBookOrdered(booksOrderedRecord)
    }

    open suspend fun addPaymentInfo(paymentInfoRecord: PaymentInfoRecord){
        RoomInstance.getDatabase(context).userDao().addPaymentInfo(paymentInfoRecord)
    }

}