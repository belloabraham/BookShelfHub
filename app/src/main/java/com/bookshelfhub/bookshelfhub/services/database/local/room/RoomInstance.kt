package com.bookshelfhub.bookshelfhub.services.database.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.User

@Database(entities = [User::class], version = 1, exportSchema = false)
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