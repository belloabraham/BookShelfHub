package com.bookshelfhub.bookshelfhub.data.sources.local

import com.bookshelfhub.bookshelfhub.data.models.entities.Bookmark
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import com.google.common.truth.Truth.assertThat


import org.junit.After
import org.junit.Before
import org.junit.Test

class BookmarksDaoTest {

    private lateinit var appDatabase: AppDatabase
    private lateinit var bookmarksDao: BookmarksDao

    @Before
    fun setup(){
        appDatabase = Database.getAppDatabase()
        bookmarksDao = appDatabase.getBookmarksDao()
    }

    @After
    fun teardown(){
        appDatabase.close()
    }

    @Test
    fun addBookmark() = runBlocking {
        val bookmark = Bookmark("1", "jkhfjgk", 1, "kgjjh")
        bookmarksDao.insertOrReplace(bookmark)
        bookmarksDao.insertOrIgnore(bookmark)
        assertThat(bookmarksDao.getBookmark(1, "1")).isPresent()
        assertThat(bookmarksDao.getLocalBookmarks(false, false)).isNotEmpty()
        bookmarksDao.delete(bookmark)
        assertThat(bookmarksDao.getBookmark(1, "1")).isAbsent()
    }

}