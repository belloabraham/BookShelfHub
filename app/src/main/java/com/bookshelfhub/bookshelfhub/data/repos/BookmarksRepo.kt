package com.bookshelfhub.bookshelfhub.data.repos

import androidx.lifecycle.LiveData
import com.bookshelfhub.bookshelfhub.data.models.entities.Bookmark
import com.bookshelfhub.bookshelfhub.data.repos.sources.local.BookmarksDao
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.IRemoteDataSource
import com.bookshelfhub.bookshelfhub.workers.Worker
import com.google.common.base.Optional
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BookmarksRepo @Inject constructor(
    private val bookmarksDao: BookmarksDao, private val worker: Worker, private val remoteDataS: IRemoteDataSource) {

     suspend fun getBookmarks(deleted: Boolean): List<Bookmark> {
        return withContext(IO){bookmarksDao.getBookmarks(deleted)}
    }

     suspend fun getBookmark(pageNumb: Int, isbn: String): Optional<Bookmark> {
        return  withContext(IO){bookmarksDao.getBookmark(pageNumb, isbn)}
    }

     suspend fun deleteFromBookmark(pageNumb: Int, isbn:String) {
         withContext(IO){bookmarksDao.deleteFromBookmark(pageNumb, isbn)}
    }

     suspend fun addBookmark(bookmark: Bookmark) {
         withContext(IO){bookmarksDao.addBookmark(bookmark)}
    }

     suspend fun getDeletedBookmarks(deleted: Boolean, uploaded: Boolean): List<Bookmark> {
        return withContext(IO){bookmarksDao.getDeletedBookmarks(deleted, uploaded)}
    }

     suspend fun deleteAllBookmarks() {
        return withContext(IO){bookmarksDao.deleteAllBookmarks()}
    }

     suspend fun addBookmarkList(bookmarks: List<Bookmark>) {
         withContext(IO){bookmarksDao.addBookmarkList(bookmarks)}
    }

     suspend fun deleteBookmarks(bookmarks: List<Bookmark>) {
         withContext(IO){bookmarksDao.deleteBookmarks(bookmarks)}
    }

     suspend fun getLocalBookmarks(
        uploaded: Boolean,
        deleted: Boolean
    ): List<Bookmark> {
        return  withContext(IO){bookmarksDao.getLocalBookmarks(uploaded, deleted)}
    }

     fun getLiveBookmarks(deleted: Boolean): LiveData<List<Bookmark>> {
        return  bookmarksDao.getLiveBookmarks( deleted)
    }


}