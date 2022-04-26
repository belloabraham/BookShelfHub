package com.bookshelfhub.bookshelfhub.data.repos.bookmarks

import androidx.lifecycle.LiveData
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import com.bookshelfhub.bookshelfhub.data.models.entities.Bookmark
import com.bookshelfhub.bookshelfhub.data.models.entities.IEntityId
import com.bookshelfhub.bookshelfhub.data.sources.local.BookmarksDao
import com.bookshelfhub.bookshelfhub.data.sources.local.RoomInstance
import com.bookshelfhub.bookshelfhub.data.sources.remote.IRemoteDataSource
import com.bookshelfhub.bookshelfhub.data.sources.remote.RemoteDataFields
import com.bookshelfhub.bookshelfhub.workers.*
import com.google.common.base.Optional
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BookmarksRepo @Inject constructor(
    roomInstance: RoomInstance,
    private val worker: Worker,
    private val remoteDataSource: IRemoteDataSource,
    ) :
    IBookmarksRepo {
    private val bookmarksDao = roomInstance.bookmarksDao()
    private val ioDispatcher: CoroutineDispatcher = IO

     override suspend fun getBookmarks(deleted: Boolean): List<Bookmark> {
        return withContext(ioDispatcher){
          val bookmarks =  bookmarksDao.getBookmarks(deleted)
            if (bookmarks.isEmpty()){
                val oneTimeBookmarksDataDownload =
                    OneTimeWorkRequestBuilder<DownloadBookmarks>()
                        .setConstraints(Constraint.getConnected())
                        .build()
                worker.enqueueUniqueWork(Tag.oneTimeBookmarksDataDownload, ExistingWorkPolicy.KEEP, oneTimeBookmarksDataDownload)
            }
            bookmarks
        }
    }

     override suspend fun getBookmark(pageNumb: Int, isbn: String): Optional<Bookmark> {
        return  withContext(ioDispatcher){
            bookmarksDao.getBookmark(pageNumb, isbn)
        }
    }

     override suspend fun deleteFromBookmark(pageNumb: Int, isbn:String) {
         withContext(ioDispatcher){bookmarksDao.deleteFromBookmark(pageNumb, isbn)}
    }

     override suspend fun addBookmark(bookmark: Bookmark) {
         withContext(ioDispatcher){bookmarksDao.insertOrReplace(bookmark)}
    }

     override suspend fun getDeletedBookmarks(deleted: Boolean, uploaded: Boolean): List<Bookmark> {
        return withContext(ioDispatcher){bookmarksDao.getDeletedBookmarks(deleted, uploaded)}
    }

     override suspend fun deleteAllBookmarks() {
        return withContext(ioDispatcher){bookmarksDao.deleteAllBookmarks()}
    }

     override suspend fun addBookmarkList(bookmarks: List<Bookmark>) {
         withContext(ioDispatcher){bookmarksDao.insertAllOrReplace(bookmarks)}
    }

     override suspend fun deleteBookmarks(bookmarks: List<Bookmark>) {
         withContext(ioDispatcher){bookmarksDao.deleteAll(bookmarks)}
    }

     override suspend fun getLocalBookmarks(
        uploaded: Boolean,
        deleted: Boolean
    ): List<Bookmark> {
        return  withContext(ioDispatcher){bookmarksDao.getLocalBookmarks(uploaded, deleted)}
    }

    override suspend fun updateRemoteUserBookmarks(listOfBookmarks:List<Bookmark>, userId: String): Void? {
       return withContext(ioDispatcher){
           remoteDataSource.addListOfDataAsync(
               listOfBookmarks,
               RemoteDataFields.USERS_COLL,
               userId,
               RemoteDataFields.BOOKMARKS_COLL)
       }
    }

    override suspend fun deleteRemoteBookmarks(list: List<IEntityId>, userId:String): Void {
       return withContext(ioDispatcher){
           remoteDataSource.deleteListOfDataAsync(
               list,
               RemoteDataFields.USERS_COLL,
               userId,
               RemoteDataFields.BOOKMARKS_COLL)
       }
    }

   override suspend fun getRemoteBookmarks(userId: String): List<Bookmark> {
        return withContext(ioDispatcher){
            remoteDataSource.getListOfDataAsync(
            RemoteDataFields.USERS_COLL,
            userId,
            RemoteDataFields.BOOKMARKS_COLL,
            Bookmark::class.java
        )
        }
    }

     override fun getLiveBookmarks(deleted: Boolean): LiveData<List<Bookmark>> {
        return  bookmarksDao.getLiveBookmarks( deleted)
    }


}