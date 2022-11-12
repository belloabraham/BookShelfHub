package com.bookshelfhub.core.data.repos.bookmarks

import androidx.lifecycle.LiveData
import com.bookshelfhub.core.model.entities.Bookmark
import com.bookshelfhub.core.model.entities.IEntityId
import java.util.*

interface IBookmarksRepo {
    suspend fun getBookmarks(isDeleted: Boolean): List<Bookmark>

    suspend fun getBookmark(pageNumb: Int, isbn: String): Optional<Bookmark>

    suspend fun deleteFromBookmark(pageNumb: Int, isbn: String)

    suspend fun addBookmark(bookmark: Bookmark)

    suspend fun getDeletedBookmarks(isDeleted: Boolean, isUploaded: Boolean): List<Bookmark>

    suspend fun deleteAllBookmarks()

    suspend fun addBookmarkList(bookmarks: List<Bookmark>)

    suspend fun deleteBookmarks(bookmarks: List<Bookmark>)

    suspend fun getLocalBookmarks(
        isUploaded: Boolean,
        isDeleted: Boolean
): List<Bookmark>

    suspend fun updateRemoteUserBookmarks(listOfBookmarks: List<Bookmark>, userId: String): Void?

    suspend fun deleteRemoteBookmarks(list: List<IEntityId>, userId: String): Void

    suspend fun getRemoteBookmarks(userId: String): List<Bookmark>
    fun getLiveBookmarks(deleted: Boolean): LiveData<List<Bookmark>>
}