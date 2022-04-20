package com.bookshelfhub.bookshelfhub.data.repos.sources.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.bookshelfhub.bookshelfhub.data.models.entities.Bookmark
import com.google.common.base.Optional

@Dao
abstract class BookmarksDao : BaseDao<Bookmark> {
    
    @Query("SELECT * FROM Bookmark WHERE deleted = :deleted")
    abstract suspend fun getBookmarks(deleted: Boolean):List<Bookmark>

    @Query("DELETE FROM Bookmark WHERE pageNumb = :pageNumb AND isbn =:isbn")
    abstract suspend fun deleteFromBookmark(pageNumb: Int, isbn:String)

    @Query("SELECT * FROM Bookmark WHERE pageNumb = :pageNumb AND isbn =:isbn")
    abstract suspend fun getBookmark(pageNumb:Int, isbn:String): Optional<Bookmark>

    @Query("SELECT * FROM Bookmark WHERE deleted = :deleted")
    abstract fun getLiveBookmarks(deleted: Boolean): LiveData<List<Bookmark>>

    @Query("DELETE FROM Bookmark")
    abstract suspend fun deleteAllBookmarks()

    @Query("SELECT * FROM Bookmark WHERE deleted =:deleted AND uploaded =:uploaded")
    abstract suspend fun getDeletedBookmarks(deleted: Boolean, uploaded: Boolean):List<Bookmark>

    @Query("SELECT * FROM Bookmark WHERE uploaded = :uploaded AND deleted =:deleted")
    abstract suspend fun getLocalBookmarks(uploaded:Boolean, deleted: Boolean):List<Bookmark>
}