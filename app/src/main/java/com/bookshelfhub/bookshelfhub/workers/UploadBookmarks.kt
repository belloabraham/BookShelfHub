package com.bookshelfhub.bookshelfhub.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bookshelfhub.bookshelfhub.enums.DbFields
import com.bookshelfhub.bookshelfhub.services.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.services.database.cloud.ICloudDb
import com.bookshelfhub.bookshelfhub.services.database.local.ILocalDb
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.coroutineScope

@HiltWorker
class UploadBookmarks @AssistedInject constructor(
    @Assisted val context: Context, @Assisted workerParams: WorkerParameters,
    private val localDb: ILocalDb, private val cloudDb: ICloudDb, private val userAuth: IUserAuth
): CoroutineWorker(context,
workerParams
){
    override suspend fun doWork(): Result {

        if (!userAuth.getIsUserAuthenticated()){
            return Result.retry()
        }

        val userId = userAuth.getUserId()

        val listOfBookmarks = localDb.getLocalBookmarks(userId,false)

        cloudDb.addListOfDataAsync(listOfBookmarks, DbFields.USERS.KEY, userId,  DbFields.BOOKMARKS.KEY, DbFields.BOOKMARK.KEY){

            val length = listOfBookmarks.size-1

                for (i in 0..length){
                    listOfBookmarks[i].uploaded = true
                }

                coroutineScope {
                    localDb.addBookmarkList(listOfBookmarks)
                }
        }

        return Result.success()
    }

}