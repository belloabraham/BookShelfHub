package com.bookshelfhub.bookshelfhub.services.database.local.room

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.*
import com.google.common.base.Optional

@Dao
interface UserDao {

    //Todo Bookmarks
    @Query("SELECT * FROM Bookmark")
    fun getLiveBookmarks():LiveData<List<Bookmark>>

    @Query("SELECT * FROM Bookmark WHERE uploaded = :uploaded")
    suspend fun getLocalBookmarks(uploaded:Boolean):List<Bookmark>

    @Query("UPDATE Bookmark SET uploaded =:uploaded WHERE id in (:idList)")
    suspend fun updateLocalBookmarks(uploaded:Boolean=true, idList: List<Long>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addBookmark(bookmark: Bookmark)

    @Delete
    suspend fun deleteBookmark(bookmark: Bookmark)

    //Todo Cart
    @Query("SELECT COUNT(*) FROM Cart")
    fun getLiveTotalCartItemsNo(): LiveData<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToCart(cart:Cart)

    @Query("SELECT * FROM Cart")
    fun getLiveCartItems():LiveData<List<Cart>>

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
    fun getLiveShelfSearchHistory(userId:String): LiveData<List<ShelfSearchHistory>>

    @Query("SELECT * FROM StoreSearchHistory WHERE userId = :userId Order BY dateTime DESC LIMIT 4")
    fun getLiveStoreSearchHistory(userId:String): LiveData<List<StoreSearchHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addStoreSearchHistory(searchHistory:StoreSearchHistory)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addShelfSearchHistory(shelfSearchHistory:ShelfSearchHistory)

    //Todo Referral
    @Query("SELECT * FROM PubReferrers WHERE isbn = :isbn")
    suspend fun getPubReferrer(isbn:String): Optional<PubReferrers>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPubReferrer(pubReferrers: PubReferrers)

    //Todo PublishedBooks
    @Query("DELETE FROM PublishedBooks WHERE isbn in (:isbnList)")
    fun deleteUnPublishedBookRecords(isbnList: List<String>)

    @Query("UPDATE PublishedBooks SET recommended = :recommend WHERE category =:category")
    suspend fun updateRecommendedBooksByCategory(category: String, recommend:Boolean)

    @Query("UPDATE PublishedBooks SET recommended = :recommend  WHERE tag = :tag")
    suspend fun updateRecommendedBooksByTag(tag: String, recommend:Boolean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAllPubBooks(publishedBooks: List<PublishedBooks>)

    @Query("SELECT * FROM PublishedBooks ORDER BY dateTimePublished DESC")
    fun getLivePublishedBooks(): LiveData<List<PublishedBooks>>

    @Query("SELECT * FROM PublishedBooks ORDER BY dateTimePublished DESC")
    fun getPublishedBooks(): List<PublishedBooks>

    @Query("SELECT * FROM PublishedBooks WHERE category = :category ORDER BY dateTimePublished DESC")
    fun getLiveBooksByCategory(category:String): LiveData<List<PublishedBooks>>

    @Query("SELECT * FROM PublishedBooks WHERE recommended = :recommend ORDER BY dateTimePublished DESC")
    fun getLiveRecommendedBooks(recommend:Boolean=true): LiveData<List<PublishedBooks>>

    @Query("SELECT * FROM PublishedBooks ORDER BY noOfDownloads DESC LIMIT 100")
    fun getLiveTrendingBooks(): LiveData<List<PublishedBooks>>

    @Query("SELECT * FROM PublishedBooks WHERE category = :category ORDER BY dateTimePublished DESC")
    fun getBooksByCategoryPageSource(category:String): PagingSource<Int, PublishedBooks>

    @Query("SELECT * FROM PublishedBooks ORDER BY noOfDownloads DESC LIMIT 100")
    fun getTrendingBooksPageSource(): PagingSource<Int, PublishedBooks>

    @Query("SELECT * FROM PublishedBooks WHERE recommended = :recommend   ORDER BY noOfDownloads DESC")
    fun getRecommendedBooksPageSource(recommend:Boolean=true): PagingSource<Int, PublishedBooks>
}