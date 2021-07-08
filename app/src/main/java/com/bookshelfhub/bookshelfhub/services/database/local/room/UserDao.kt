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
    suspend fun addPaymentInfo(paymentInfo:PaymentInfo)

    @Query("SELECT * FROM PaymentInfo WHERE userId = :userId")
    fun getLivePaymentInfo(userId:String): LiveData<PaymentInfo>

    //Todo User Record
    @Query("SELECT * FROM User WHERE userId = :userId")
    suspend fun getUser(userId:String): Optional<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUser(user:User)

    @Query("SELECT * FROM User WHERE userId = :userId")
    fun getLiveUser(userId:String): LiveData<User>


    //Todo Book Interest
    @Query("SELECT * FROM BookInterest WHERE userId = :userId")
    suspend fun getBookInterest(userId:String): Optional<BookInterest>

    @Query("SELECT * FROM BookInterest WHERE userId = :userId")
    fun getLiveBookInterest(userId:String): LiveData<Optional<BookInterest>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addBookInterest(bookInterest: BookInterest)

    //Todo Ordered Books
    @Query("SELECT * FROM OrderedBooks WHERE userId = :userId")
    fun getLiveBooksOrdered(userId:String): LiveData<List<OrderedBooks>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addBookOrdered(paymentInfo:OrderedBooks)


    //Todo Search History
    @Query("SELECT * FROM ShelfSearchHistory WHERE userId = :userId Order BY dateTime DESC LIMIT 4")
    fun getLiveShelfSearchHistory(userId:String): LiveData<List<ShelfSearchResult>>

    @Query("SELECT * FROM StoreSearchHistory WHERE userId = :userId Order BY dateTime DESC LIMIT 4")
    fun getLiveStoreSearchHistory(userId:String): LiveData<List<StoreSearchResult>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addStoreSearchHistory(searchHistory:StoreSearchResult)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addShelfSearchHistory(shelfSearchHistory:ShelfSearchResult)

    //Todo Referral
    @Query("SELECT * FROM PubReferrers WHERE isbn = :isbn")
    suspend fun getPubReferrer(isbn:String): Optional<PubReferrers>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPubReferrer(pubReferrers: PubReferrers)
}