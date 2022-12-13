package com.bookshelfhub.core.database

import android.content.Context
import androidx.room.*
import com.bookshelfhub.core.model.entities.*
import com.bookshelfhub.core.database.dao.*
import com.bookshelfhub.core.model.entities.User
import com.bookshelfhub.core.model.uistate.BookDownloadState

@Database(entities = [User::class, OrderedBook::class, BookInterest::class, StoreSearchHistory::class, Collaborator::class, PublishedBook::class, PaymentCard::class, CartItem::class, UserReview::class, ReadHistory::class, Bookmark::class, PaymentTransaction::class, BookDownloadState::class], version = 1, exportSchema = false)
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
                    Config.DATABASE_NAME
                ).build()
                INSTANCE =newInstance
                return newInstance
            }
        }
    }
}