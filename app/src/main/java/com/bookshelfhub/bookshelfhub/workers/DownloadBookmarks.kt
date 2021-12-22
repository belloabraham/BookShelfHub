package com.bookshelfhub.bookshelfhub.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bookshelfhub.bookshelfhub.Utils.Logger
import com.bookshelfhub.bookshelfhub.services.database.cloud.DbFields
import com.bookshelfhub.bookshelfhub.services.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.services.database.cloud.ICloudDb
import com.bookshelfhub.bookshelfhub.helpers.database.ILocalDb
import com.bookshelfhub.bookshelfhub.helpers.database.room.entities.Bookmark
import com.bookshelfhub.bookshelfhub.services.database.Util
import com.google.android.gms.tasks.Tasks
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await

@HiltWorker
class DownloadBookmarks @AssistedInject constructor(
    @Assisted val context: Context, @Assisted workerParams: WorkerParameters,
    private val localDb: ILocalDb,
    private val cloudDb: ICloudDb,
    private val util: Util,
    private val userAuth: IUserAuth
): CoroutineWorker(context,
workerParams
){
    override suspend fun doWork(): Result {

        val userId = userAuth.getUserId()


     return   try {
            //Get user bookmarks from the cloud using this path user/userId/bookmarks/id
            val querySnapShot = cloudDb.getListOfDataAsync(
                DbFields.USERS.KEY,
                userId,
                DbFields.BOOKMARKS.KEY
            ).await()

            val bookmarks = util.queryToListType(querySnapShot, Bookmark::class.java)

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