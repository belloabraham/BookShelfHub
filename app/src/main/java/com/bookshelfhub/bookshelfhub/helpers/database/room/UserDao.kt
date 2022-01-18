package com.bookshelfhub.bookshelfhub.helpers.database.room

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import com.bookshelfhub.bookshelfhub.helpers.database.room.entities.*
import com.bookshelfhub.bookshelfhub.helpers.database.room.entities.PaymentTransaction
import com.google.common.base.Optional

@Dao
interface UserDao {


    //Payment Card
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPaymentCard(paymentCard: PaymentCard)

    @Delete
    suspend fun deletePaymentCard(card: PaymentCard)

    @Query("DELETE FROM PaymentCard")
    suspend fun deleteAllPaymentCards()

    @Query("SELECT * FROM PaymentCard")
    fun getLivePaymentCards(): LiveData<List<PaymentCard>>

    @Query("SELECT * FROM PaymentCard")
    suspend fun getPaymentCards(): List<PaymentCard>

    //Book videos
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addBookVideos(bookVideos: List<BookVideos>)

    @Query("SELECT * FROM BookVideos WHERE isbn = :isbn")
    fun getLiveListOfBookVideos(isbn: String): LiveData<List<BookVideos>>

    //Book read history
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addReadHistory(history: History)

    @Query("SELECT * FROM History WHERE id =:id")
    fun getLiveReadHistory(id:Int):LiveData<Optional<History>>

    @Query("DELETE FROM History")
    suspend fun deleteAllHistory()

    //Payment Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPaymentTransactions(paymentTransactions: List<PaymentTransaction>)

    @Query("SELECT * FROM PaymentTransaction")
    suspend fun getAllPaymentTransactions(): List<PaymentTransaction>

    @Query("DELETE FROM PaymentTransaction")
    suspend fun deleteAllPaymentTransactions()

    //User Review
    @Query("SELECT * FROM UserReview WHERE isbn = :isbn")
    fun getLiveUserReview(isbn:String): LiveData<Optional<UserReview>>

    @Query("SELECT * FROM UserReview WHERE verified = :isVerified")
    suspend fun getUserReviews(isVerified:Boolean): List<UserReview>

    @Query("UPDATE UserReview set verified =:isVerified where isbn =:isbn")
    suspend fun updateReview(isbn: String, isVerified:Boolean)

    @Query("DELETE FROM UserReview")
    suspend fun deleteAllReviews()

    @Query("SELECT * FROM UserReview WHERE isbn = :isbn")
    suspend fun getUserReview(isbn:String): Optional<UserReview>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUserReviews(userReviews: List<UserReview>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUserReview(userReview: UserReview)


    //Bookmarks
    @Query("SELECT * FROM Bookmark WHERE deleted = :deleted")
    suspend fun getBookmarks(deleted: Boolean):List<Bookmark>

    @Query("DELETE FROM Bookmark WHERE pageNumb = :pageNumb AND isbn =:isbn")
    suspend fun deleteFromBookmark(pageNumb: Int, isbn:String)

    @Query("SELECT * FROM Bookmark WHERE pageNumb = :pageNumb AND isbn =:isbn")
    suspend fun getBookmark(pageNumb:Int, isbn:String):  Optional<Bookmark>

    @Query("SELECT * FROM Bookmark WHERE deleted = :deleted")
    fun getLiveBookmarks(deleted: Boolean): LiveData<List<Bookmark>>

    @Query("DELETE FROM Bookmark")
    suspend fun deleteAllBookmarks()

    @Query("SELECT * FROM Bookmark WHERE deleted =:deleted AND uploaded =:uploaded")
    suspend fun getDeletedBookmarks(deleted: Boolean, uploaded: Boolean):List<Bookmark>

    @Query("SELECT * FROM Bookmark WHERE uploaded = :uploaded AND deleted =:deleted")
    suspend fun getLocalBookmarks(uploaded:Boolean, deleted: Boolean):List<Bookmark>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addBookmarkList(bookmarks: List<Bookmark>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addBookmark(bookmark: Bookmark)

    @Delete
    suspend fun deleteBookmarks(bookmarks: List<Bookmark>)

    //Cart
    @Query("SELECT COUNT(*) FROM Cart WHERE userId =:userId")
    fun getLiveTotalCartItemsNo(userId: String): LiveData<Int>

    @Query("DELETE FROM Cart WHERE isbn in (:isbnList)")
    suspend fun deleteFromCart(isbnList: List<String>)

    @Query("DELETE FROM Cart")
    suspend fun deleteAllCartItems()


    @Delete
    suspend fun deleteFromCart(cart: Cart)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToCart(cart:Cart)

    @Query("SELECT * FROM Cart WHERE userId =:userId")
    fun getLiveListOfCartItems(userId: String):LiveData<List<Cart>>

    @Query("SELECT * FROM Cart WHERE userId =:userId")
    suspend fun getListOfCartItems(userId: String):List<Cart>

    //User Record
    @Query("DELETE FROM User")
    suspend fun deleteUserRecord()

    @Query("SELECT * FROM User WHERE userId = :userId")
    suspend fun getUser(userId:String): Optional<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUser(user:User)

    @Query("SELECT * FROM User WHERE userId = :userId")
    fun getLiveUser(userId:String): LiveData<User>


    //Book Interest
    @Query("SELECT * FROM BookInterest WHERE userId = :userId")
    suspend fun getBookInterest(userId:String): Optional<BookInterest>

    @Query("SELECT * FROM BookInterest WHERE userId = :userId")
    fun getLiveBookInterest(userId:String): LiveData<Optional<BookInterest>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addBookInterest(bookInterest: BookInterest)

    //Ordered Books
    @Query("SELECT * FROM OrderedBooks WHERE userId = :userId")
    fun getLiveBooksOrdered(userId:String): LiveData<List<OrderedBooks>>

    @Query("SELECT * FROM OrderedBooks WHERE isbn = :isbn")
    fun getLiveOrderedBook(isbn:String): LiveData<OrderedBooks>

    @Query("SELECT * FROM OrderedBooks WHERE userId = :userId")
    suspend fun getOrderedBooks(userId:String): List<OrderedBooks>

    @Query("SELECT * FROM OrderedBooks WHERE isbn =:isbn")
    fun getALiveOrderedBook(isbn:String): LiveData<Optional<OrderedBooks>>

    @Query("SELECT * FROM OrderedBooks WHERE isbn =:isbn")
    suspend fun getAnOrderedBook(isbn:String): OrderedBooks

    @Query("DELETE FROM OrderedBooks")
    fun deleteAllOrderedBooks()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addOrderedBooks(OrderedBooks: List<OrderedBooks>)


    //Search History
    @Query("SELECT * FROM ShelfSearchHistory WHERE userId = :userId Order BY dateTime DESC LIMIT 4")
    fun getLiveShelfSearchHistory(userId:String): LiveData<List<ShelfSearchHistory>>

    @Query("SELECT * FROM StoreSearchHistory WHERE userId = :userId Order BY dateTime DESC LIMIT 4")
    fun getLiveStoreSearchHistory(userId:String): LiveData<List<StoreSearchHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addStoreSearchHistory(searchHistory:StoreSearchHistory)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addShelfSearchHistory(shelfSearchHistory:ShelfSearchHistory)

    //Referral
    @Query("SELECT * FROM PubReferrers WHERE isbn = :isbn")
    fun getLivePubReferrer(isbn:String): LiveData<Optional<PubReferrers>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addPubReferrer(pubReferrers: PubReferrers)


    //PublishedBook
    @Query("SELECT * FROM PublishedBook WHERE isbn = :isbn")
    suspend fun getPublishedBook(isbn: String): Optional<PublishedBook>

    @Query("SELECT * FROM PublishedBook WHERE isbn = :isbn")
    fun getLivePublishedBook(isbn: String): LiveData<Optional<PublishedBook>>

    @Delete
    suspend fun deleteUnPublishedBookRecords(publishedBooks: List<PublishedBook>)


    @Query("UPDATE PublishedBook SET recommended = :recommend WHERE category =:category")
    suspend fun updateRecommendedBooksByCategory(category: String, recommend:Boolean)

    @Query("UPDATE PublishedBook SET recommended = :recommend  WHERE tag = :tag")
    suspend fun updateRecommendedBooksByTag(tag: String, recommend:Boolean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAllPubBooks(publishedBooks: List<PublishedBook>)

    @Query("SELECT * FROM PublishedBook ORDER BY publishedDate DESC")
    fun getLivePublishedBooks(): LiveData<List<PublishedBook>>

    @Query("SELECT * FROM PublishedBook ORDER BY publishedDate DESC")
    fun getPublishedBooks(): List<PublishedBook>

    @Query("SELECT * FROM PublishedBook WHERE category = :category ORDER BY publishedDate DESC")
    fun getLiveBooksByCategory(category:String): LiveData<List<PublishedBook>>

    @Query("SELECT * FROM PublishedBook WHERE recommended = :recommend ORDER BY publishedDate DESC")
    fun getLiveRecommendedBooks(recommend:Boolean=true): LiveData<List<PublishedBook>>

    @Query("SELECT * FROM PublishedBook ORDER BY totalDownloads DESC LIMIT 100")
    fun getLiveTrendingBooks(): LiveData<List<PublishedBook>>

    @Query("SELECT * FROM PublishedBook WHERE category = :category ORDER BY publishedDate DESC")
    fun getBooksByCategoryPageSource(category:String): PagingSource<Int, PublishedBook>

    @Query("SELECT * FROM PublishedBook ORDER BY totalDownloads DESC LIMIT 100")
    fun getTrendingBooksPageSource(): PagingSource<Int, PublishedBook>

    @Query("SELECT * FROM PublishedBook WHERE recommended = :recommend   ORDER BY totalDownloads DESC")
    fun getRecommendedBooksPageSource(recommend:Boolean=true): PagingSource<Int, PublishedBook>
}