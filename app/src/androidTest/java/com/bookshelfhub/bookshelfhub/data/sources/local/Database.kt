package com.bookshelfhub.bookshelfhub.data.sources.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider

object Database {

    fun getAppDatabase(): AppDatabase {
      return  Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
    }
}