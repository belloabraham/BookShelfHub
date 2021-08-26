package com.bookshelfhub.bookshelfhub.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bookshelfhub.bookshelfhub.services.database.cloud.DbFields
import com.bookshelfhub.bookshelfhub.services.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.services.database.cloud.ICloudDb
import com.bookshelfhub.bookshelfhub.services.database.local.ILocalDb
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.coroutineScope

@HiltWorker
class DeleteBookmarks @AssistedInject constructor(
    @Assisted val context: Context, @Assisted workerParams: WorkerParameters,
    private val localDb: ILocalDb, private val cloudDb: ICloudDb, private val userAuth: IUserAuth
): CoroutineWorker(context,
workerParams
){
    override suspend fun doWork(): Result {

        val userId = userAuth.getUserId()

        //Get locally deleted bookmarks
        val localListOfDeletedBookmarks  = localDb.getDeletedBookmarks(deleted = true, uploaded = false)

        //Remove theme locally
        if (localListOfDeletedBookmarks.isNotEmpty()){
            localDb.deleteBookmarks(localListOfDeletedBookmarks)
        }

        //Get locally deleted bookmarks that are already on the cloud
        val listOfDeletedBookmarks  = localDb.getDeletedBookmarks(deleted = true, uploaded = true)

        //Delete them on the cloud using this path user/userId/bookmarks/id
        cloudDb.deleteListOfDataAsync(listOfDeletedBookmarks, DbFields.USERS.KEY, userId, DbFields.BOOKMARKS.KEY){

                coroutineScope {
                    //Now remove them locally
                    localDb.deleteBookmarks(listOfDeletedBookmarks)
                }
        }

        return Result.success()
    }

}