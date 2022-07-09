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
        val bookmark = Bookmark("1", "2", 1, "kgjjh")
        bookmarksDao.insertOrIgnore(bookmark)

        assertThat(bookmarksDao.getBookmark(1, "2")).isPresent()
        assertThat(bookmarksDao.getLocalBookmarks(false, false)).isNotEmpty()

        bookmarksDao.deleteFromBookmark(1, "2")
        assertThat(bookmarksDao.getBookmark(1, "2")).isAbsent()

        val bookmark2 = bookmark.copy(bookId = "3", deleted = true)
        bookmarksDao.insertOrReplace(bookmark2)

        assertThat(bookmarksDao.getDeletedBookmarks(true, false)).isNotEmpty()
        bookmarksDao.deleteAllBookmarks()
        assertThat(bookmarksDao.getBookmark(1, "3")).isAbsent()

    }


}