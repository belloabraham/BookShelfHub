package com.bookshelfhub.bookshelfhub.services.database.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.BookInterestRecord
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.BooksOrderedRecord
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.PaymentInfoRecord
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.UserRecord
import com.google.common.base.Optional

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUser(userRecord:UserRecord)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPaymentInfo(paymentInfo:PaymentInfoRecord)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addBookOrdered(paymentInfo:BooksOrderedRecord)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addBookInterest(bookInterest: BookInterestRecord)

    @Query("SELECT * FROM User WHERE userId = :userId")
    fun getUser(userId:String): Optional<UserRecord>

    @Query("SELECT * FROM BookInterest WHERE userId = :userId")
    fun getBookInterest(userId:String): Optional<BookInterestRecord>

    @Query("SELECT * FROM User WHERE userId = :userId")
    fun getLiveUser(userId:String): LiveData<UserRecord>

    @Query("SELECT * FROM PaymentInfo WHERE userId = :userId")
    fun getLivePaymentInfo(userId:String): LiveData<PaymentInfoRecord>

    @Query("SELECT * FROM BooksOrdered WHERE userId = :userId")
    fun getLiveBooksOrdered(userId:String): LiveData<BooksOrderedRecord>

}