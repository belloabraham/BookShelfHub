package com.bookshelfhub.bookshelfhub.data.repos.bookmarks

import androidx.lifecycle.LiveData
import com.bookshelfhub.bookshelfhub.data.models.entities.Bookmark
import com.bookshelfhub.bookshelfhub.data.models.entities.IEntityId
import com.google.common.base.Optional

interface IBookmarksRepo {
    suspend fun getBookmarks(deleted: Boolean): List<Bookmark>

    suspend fun getBookmark(pageNumb: Int, isbn: String): Optional<Bookmark>

    suspend fun deleteFromBookmark(pageNumb: Int, isbn: String)

    suspend fun addBookmark(bookmark: Bookmark)

    suspend fun getDeletedBookmarks(deleted: Boolean, uploaded: Boolean): List<Bookmark>

    suspend fun deleteAllBookmarks()

    suspend fun addBookmarkList(bookmarks: List<Bookmark>)

    suspend fun deleteBookmarks(bookmarks: List<Bookmark>)

    suspend fun getLocalBookmarks(
        uploaded: Boolean,
        deleted: Boolean
): List<Bookmark>

    suspend fun updateRemoteUserBookmarks(listOfBookmarks: List<Bookmark>, userId: String): Void?

    suspend fun deleteRemoteBookmarks(list: List<IEntityId>, userId: String): Void

    suspend fun getRemoteBookmarks(userId: String): List<Bookmark>
    fun getLiveBookmarks(deleted: Boolean): LiveData<List<Bookmark>>
}