package com.bookshelfhub.core.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.bookshelfhub.core.model.entities.Bookmark
import java.util.*

@Dao
abstract class BookmarksDao : BaseDao<Bookmark> {
    
    @Query("SELECT * FROM Bookmarks WHERE deleted = :deleted")
    abstract suspend fun getBookmarks(deleted: Boolean):List<Bookmark>

    @Query("UPDATE Bookmarks SET deleted =:isDeleted WHERE pageNumb=:pageNumb AND bookId =:bookId")
    abstract suspend fun markBookmark(isDeleted: Boolean, pageNumb: Int, bookId:String)

    @Query("SELECT * FROM Bookmarks WHERE pageNumb = :pageNumb AND bookId =:bookId AND deleted =:isDeleted")
    abstract suspend fun getBookmark(pageNumb:Int, bookId:String, isDeleted:Boolean): Optional<Bookmark>

    @Query("SELECT * FROM Bookmarks WHERE deleted = :deleted")
    abstract fun getLiveBookmarks(deleted: Boolean): LiveData<List<Bookmark>>

    @Query("DELETE FROM Bookmarks")
    abstract suspend fun deleteAllBookmarks()

    @Query("SELECT * FROM Bookmarks WHERE deleted =:deleted AND uploaded =:uploaded")
    abstract suspend fun getDeletedBookmarks(deleted: Boolean, uploaded: Boolean):List<Bookmark>

    @Query("SELECT * FROM Bookmarks WHERE uploaded = :uploaded AND deleted =:deleted")
    abstract suspend fun getLocalBookmarks(uploaded:Boolean, deleted: Boolean):List<Bookmark>
}