package com.bookshelfhub.bookshelfhub.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bookshelfhub.bookshelfhub.services.database.cloud.DbFields
import com.bookshelfhub.bookshelfhub.services.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.services.database.cloud.ICloudDb
import com.bookshelfhub.bookshelfhub.helpers.database.ILocalDb
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

        val listOfBookmarks = localDb.getLocalBookmarks(uploaded = false, deleted = false)

     return   if (listOfBookmarks.isNotEmpty()){
            val userId = userAuth.getUserId()
         val  task =  cloudDb.addListOfDataAsync(listOfBookmarks, DbFields.USERS.KEY, userId,  DbFields.BOOKMARKS.KEY)


            if(task.isSuccessful){

                val length = listOfBookmarks.size-1

                for (i in 0..length){
                    listOfBookmarks[i].uploaded = true
                }

                localDb.addBookmarkList(listOfBookmarks)

                Result.success()
            }else{
                Result.retry()
            }

        }else{
            Result.success()
        }

    }

}