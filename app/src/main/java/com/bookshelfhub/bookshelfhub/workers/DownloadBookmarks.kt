package com.bookshelfhub.bookshelfhub.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bookshelfhub.bookshelfhub.helpers.utils.Logger
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.RemoteDataFields
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.IRemoteDataSource
import com.bookshelfhub.bookshelfhub.helpers.database.ILocalDb
import com.bookshelfhub.bookshelfhub.data.models.entities.Bookmark
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.Util
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.tasks.await

@HiltWorker
class DownloadBookmarks @AssistedInject constructor(
    @Assisted val context: Context, @Assisted workerParams: WorkerParameters,
    private val localDb: ILocalDb,
    private val remoteDataSource: IRemoteDataSource,
    private val util: Util,
    private val userAuth: IUserAuth
): CoroutineWorker(context,
workerParams
){
    override suspend fun doWork(): Result {

        val userId = userAuth.getUserId()


     return   try {
            //Get user bookmarks from the cloud using this path user/userId/bookmarks/id
            val querySnapShot = remoteDataSource.getListOfDataAsync(
                RemoteDataFields.USERS_COLL,
                userId,
                RemoteDataFields.BOOKMARKS_COLL
            ).await()

            val bookmarks = util.queryToListOfType(querySnapShot, Bookmark::class.java)

            if (bookmarks.isNotEmpty()) {
                val length = bookmarks.size - 1

                for (i in 0..length) {
                    bookmarks[i].uploaded = true
                }
                localDb.addBookmarkList(bookmarks)
            }

            Result.success()

        } catch (e: Exception) {
         Logger.log("Worker:DownldBookmarks", e)
         Result.retry()
        }

    }

}