package com.bookshelfhub.bookshelfhub.helpers.database.room

import android.content.Context
import androidx.room.*
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.helpers.database.room.entities.*
import com.bookshelfhub.bookshelfhub.helpers.database.room.entities.PaymentTransaction

@Database(entities = [User::class, OrderedBooks::class, BookInterest::class, StoreSearchHistory::class, ShelfSearchHistory::class, PubReferrers::class, PublishedBook::class, PaymentCard::class, Cart::class, UserReview::class, History::class, Bookmark::class, PaymentTransaction::class, BookVideos::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class RoomInstance: RoomDatabase() {

    abstract fun userDao():UserDao

    companion object{
        @Volatile
        private var INSTANCE:RoomInstance?=null
        fun getDatabase(context:Context):RoomInstance{
            if (INSTANCE!=null){
                return  INSTANCE!!
            }
            synchronized(this){
                val newInstance = Room.databaseBuilder(
                    context.applicationContext,
                    RoomInstance::class.java,
                    context.getString(R.string.db_name)
                ).build()
                INSTANCE=newInstance
                return newInstance
            }
        }
    }
}