package com.bookshelfhub.bookshelfhub.services.database.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.*
import com.google.common.base.Optional

@Dao
interface UserDao {


    //Todo Payment Info
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPaymentInfo(paymentInfo:PaymentInfoRecord)

    @Query("SELECT * FROM PaymentInfo WHERE userId = :userId")
    fun getLivePaymentInfo(userId:String): LiveData<PaymentInfoRecord>

    //Todo User Record
    @Query("SELECT * FROM User WHERE userId = :userId")
    fun getUser(userId:String): Optional<UserRecord>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUser(userRecord:UserRecord)

    @Query("SELECT * FROM User WHERE userId = :userId")
    fun getLiveUser(userId:String): LiveData<UserRecord>


    //Todo Book Interest
    @Query("SELECT * FROM BookInterest WHERE userId = :userId")
    fun getBookInterest(userId:String): Optional<BookInterestRecord>

    @Query("SELECT * FROM BookInterest WHERE userId = :userId")
    fun getLiveBookInterest(userId:String): LiveData<Optional<BookInterestRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addBookInterest(bookInterest: BookInterestRecord)


    //Todo Ordered Books
    @Query("SELECT * FROM BooksOrdered WHERE userId = :userId")
    fun getLiveBooksOrdered(userId:String): LiveData<BooksOrderedRecord>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addBookOrdered(paymentInfo:BooksOrderedRecord)


    //Todo Search History
    @Query("SELECT * FROM ShelfSearchHistory WHERE userId = :userId Order BY id DESC LIMIT 4")
    fun getShelfSearchHistory(userId:String): LiveData<List<ShelfSearchHistory>>

    @Query("SELECT * FROM StoreSearchHistory WHERE userId = :userId Order BY id DESC LIMIT 4")
    fun getStoreSearchHistory(userId:String): LiveData<List<StoreSearchHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addStoreSearchHistory(searchHistory:StoreSearchHistory)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addShelfSearchHistory(searchHistory:ShelfSearchHistory)

    //Todo Referral
    @Query("SELECT * FROM PubReferrers WHERE isbn = :isbn")
    fun getPubReferrer(isbn:String): Optional<PubReferrers>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPubReferrer(pubReferrers: PubReferrers)
}