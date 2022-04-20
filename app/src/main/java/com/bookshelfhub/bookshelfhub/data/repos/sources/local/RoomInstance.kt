package com.bookshelfhub.bookshelfhub.data.repos.sources.local

import android.content.Context
import androidx.room.*
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.data.models.entities.*
import com.bookshelfhub.bookshelfhub.data.models.entities.PaymentTransaction

@Database(entities = [User::class, OrderedBook::class, BookInterest::class, StoreSearchHistory::class, ShelfSearchHistory::class, Collaborator::class, PublishedBook::class, PaymentCard::class, CartItem::class, UserReview::class, ReadHistory::class, Bookmark::class, PaymentTransaction::class, BookVideo::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class RoomInstance: RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun orderedBooksDao(): OrderedBooksDao
    abstract fun bookInterestDao(): BookInterestDao
    abstract fun cartItemsDao(): CartItemsDao
    abstract fun paymentCardDao(): PaymentCardDao
    abstract fun paymentTransDao(): PaymentTransactionDao
    abstract fun publishedBooksDao(): PublishedBooksDao
    abstract fun readHistoryDao(): ReadHistoryDao
    abstract fun referralDao(): ReferralDao
    abstract fun userReviewsDao(): UserReviewDao
    abstract fun searchHistoryDao(): SearchHistoryDao
    abstract fun bookVideosDao(): BookVideosDao
    abstract fun bookmarksDao(): BookmarksDao

    companion object{
        @Volatile
        private var INSTANCE: RoomInstance?=null
        fun getDatabase(context:Context): RoomInstance {
            if (INSTANCE !=null){
                return  INSTANCE!!
            }
            synchronized(this){
                val newInstance = Room.databaseBuilder(
                    context.applicationContext,
                    RoomInstance::class.java,
                    context.getString(R.string.db_name)
                ).build()
                INSTANCE =newInstance
                return newInstance
            }
        }
    }
}