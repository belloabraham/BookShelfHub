package com.bookshelfhub.bookshelfhub.data.models.entities

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class BookmarkTest {

    @Test(expected = IllegalArgumentException::class)
    fun userIdCanNotBeEmptyOrBlank(){
        Bookmark("","3465789", 0, "gjjkhk")
    }

    @Test(expected = IllegalArgumentException::class)
    fun bookIdCanNotBeEmptyOrBlank(){
        Bookmark("56789","   ", 0, "gjjkhk")
    }

    @Test(expected = IllegalArgumentException::class)
    fun bookmarkTitleCanNotBeEmptyOrBlank(){
        Bookmark("56789","6hfggj8", 0, " ")
    }

    @Test
    fun isBookmarkDeletedDefaultsToFalse(){
        val bookmark = Bookmark("56789","6hfggj8", 0, "fgjhk")
        assertThat(bookmark.deleted).isFalse()
    }

    @Test
    fun isBookmarkUploadedDefaultsToFalse(){
        val bookmark = Bookmark("56789","6hfggj8", 0, "fgjhk")
        assertThat(bookmark.uploaded).isFalse()
    }

}