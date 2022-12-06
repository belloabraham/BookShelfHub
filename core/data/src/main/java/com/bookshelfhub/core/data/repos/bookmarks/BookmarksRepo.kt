package com.bookshelfhub.core.data.repos.bookmarks

import androidx.lifecycle.LiveData
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import com.bookshelfhub.core.common.worker.Constraint
import com.bookshelfhub.core.common.worker.Tag
import com.bookshelfhub.core.database.AppDatabase
import com.bookshelfhub.core.model.entities.Bookmark
import com.bookshelfhub.core.model.entities.IEntityId
import com.bookshelfhub.core.remote.database.IRemoteDataSource
import com.bookshelfhub.core.remote.database.RemoteDataFields
import com.bookshelfhub.core.common.worker.Worker
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class BookmarksRepo @Inject constructor(
    appDatabase: AppDatabase,
    private val worker: Worker,
    private val remoteDataSource: IRemoteDataSource,
    ) : IBookmarksRepo {
    private val ioDispatcher: CoroutineDispatcher = IO
    private val bookmarksDao= appDatabase.getBookmarksDao()

     override suspend fun getBookmarks(isDeleted: Boolean): List<Bookmark> {
        return withContext(ioDispatcher){
          val bookmarks =  bookmarksDao.getBookmarks(isDeleted)

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
         return withContext(ioDispatcher){bookmarksDao.deleteFromBookmark(pageNumb, isbn)}
    }

     override suspend fun addBookmark(bookmark: Bookmark) {
         withContext(ioDispatcher) {
             bookmarksDao.insertOrReplace(bookmark)
         }
             val oneTimeBookmarkUpload =
                 OneTimeWorkRequestBuilder<UploadBookmarks>()
                     .setConstraints(Constraint.getConnected())
                     .build()
             worker.enqueueUniqueWork(Tag.oneTimeBookmarkUpload, ExistingWorkPolicy.REPLACE, oneTimeBookmarkUpload)
    }

     override suspend fun getDeletedBookmarks(isDeleted: Boolean, isUploaded: Boolean): List<Bookmark> {
        return withContext(ioDispatcher){bookmarksDao.getDeletedBookmarks(isDeleted, isUploaded)}
    }

     override suspend fun deleteAllBookmarks() {
        return withContext(ioDispatcher){bookmarksDao.deleteAllBookmarks()}
    }

     override suspend fun addBookmarkList(bookmarks: List<Bookmark>) {
        return withContext(ioDispatcher){bookmarksDao.insertAllOrReplace(bookmarks)}
    }

     override suspend fun deleteBookmarks(bookmarks: List<Bookmark>) {
        return withContext(ioDispatcher){bookmarksDao.deleteAll(bookmarks)}
    }

     override suspend fun getLocalBookmarks(
         isUploaded: Boolean,
         isDeleted: Boolean
    ): List<Bookmark> {
        return  withContext(ioDispatcher){bookmarksDao.getLocalBookmarks(isUploaded, isDeleted)}
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