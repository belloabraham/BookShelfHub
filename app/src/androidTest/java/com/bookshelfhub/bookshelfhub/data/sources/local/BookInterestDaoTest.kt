package com.bookshelfhub.bookshelfhub.data.sources.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bookshelfhub.bookshelfhub.data.models.entities.BookInterest
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BookInterestDaoTest{

    private lateinit var appDatabase: AppDatabase
    private lateinit var bookInterestDao: BookInterestDao

    @Before
    fun setup(){
        appDatabase = Database.getAppDatabase()
        bookInterestDao = appDatabase.getBookInterestDao()
    }

    @After
    fun teardown(){
        appDatabase.close()
    }

    @Test
    fun addBookInterest() = runBlocking{
        //bookInterestDao.insertOrIgnore(BookInterest())
    }
}