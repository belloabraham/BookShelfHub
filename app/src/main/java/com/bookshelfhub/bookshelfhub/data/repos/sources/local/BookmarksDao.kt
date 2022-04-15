package com.bookshelfhub.bookshelfhub.data.repos.sources.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.bookshelfhub.bookshelfhub.data.models.entities.Bookmark
import com.google.common.base.Optional

@Dao
interface BookmarksDao {
    @Query("SELECT * FROM Bookmark WHERE deleted = :deleted")
    suspend fun getBookmarks(deleted: Boolean):List<Bookmark>

    @Query("DELETE FROM Bookmark WHERE pageNumb = :pageNumb AND isbn =:isbn")
    suspend fun deleteFromBookmark(pageNumb: Int, isbn:String)

    @Query("SELECT * FROM Bookmark WHERE pageNumb = :pageNumb AND isbn =:isbn")
    suspend fun getBookmark(pageNumb:Int, isbn:String): Optional<Bookmark>

    @Query("SELECT * FROM Bookmark WHERE deleted = :deleted")
    fun getLiveBookmarks(deleted: Boolean): LiveData<List<Bookmark>>

    @Query("DELETE FROM Bookmark")
    suspend fun deleteAllBookmarks()

    @Query("SELECT * FROM Bookmark WHERE deleted =:deleted AND uploaded =:uploaded")
    suspend fun getDeletedBookmarks(deleted: Boolean, uploaded: Boolean):List<Bookmark>

    @Query("SELECT * FROM Bookmark WHERE uploaded = :uploaded AND deleted =:deleted")
    suspend fun getLocalBookmarks(uploaded:Boolean, deleted: Boolean):List<Bookmark>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addBookmarkList(bookmarks: List<Bookmark>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addBookmark(bookmark: Bookmark)

    @Delete
    suspend fun deleteBookmarks(bookmarks: List<Bookmark>)
}