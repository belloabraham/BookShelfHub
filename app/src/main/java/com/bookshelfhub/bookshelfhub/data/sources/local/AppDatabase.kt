package com.bookshelfhub.bookshelfhub.data.sources.local

import android.content.Context
import androidx.room.*
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.data.models.entities.*
import com.bookshelfhub.bookshelfhub.data.models.entities.PaymentTransaction
import com.bookshelfhub.bookshelfhub.data.models.uistate.BookDownloadState

@Database(entities = [User::class, OrderedBook::class, BookInterest::class, StoreSearchHistory::class, ShelfSearchHistory::class, Collaborator::class, PublishedBook::class, PaymentCard::class, CartItem::class, UserReview::class, ReadHistory::class, Bookmark::class, PaymentTransaction::class, BookVideo::class, BookDownloadState::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {

    abstract fun getUserDao(): UserDao
    abstract fun getOrderedBooksDao(): OrderedBooksDao
    abstract fun getBookInterestDao(): BookInterestDao
    abstract fun getCartItemsDao(): CartItemsDao
    abstract fun getPaymentCardDao(): PaymentCardDao
    abstract fun getPaymentTransDao(): PaymentTransactionDao
    abstract fun getPublishedBooksDao(): PublishedBooksDao
    abstract fun getReadHistoryDao(): ReadHistoryDao
    abstract fun getReferralDao(): ReferralDao
    abstract fun getUserReviewsDao(): UserReviewDao
    abstract fun getSearchHistoryDao(): SearchHistoryDao
    abstract fun getBookVideosDao(): BookVideosDao
    abstract fun getBookmarksDao(): BookmarksDao
    abstract fun getBookDownloadStateDao(): BookDownloadDao

    companion object{
        @Volatile
        private var INSTANCE: AppDatabase?=null
        fun getDatabase(context:Context): AppDatabase {
            if (INSTANCE !=null){
                return  INSTANCE!!
            }
            synchronized(this){
                val newInstance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    context.getString(R.string.db_name)
                ).build()
                INSTANCE =newInstance
                return newInstance
            }
        }
    }
}