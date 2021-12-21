package com.bookshelfhub.bookshelfhub.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bookshelfhub.bookshelfhub.services.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.services.database.cloud.DbFields
import com.bookshelfhub.bookshelfhub.services.database.cloud.ICloudDb
import com.bookshelfhub.bookshelfhub.workers.Tag.NOTIFICATION_TOKEN
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class UploadNotificationToken @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val userAuth: IUserAuth,
    private val cloudDb: ICloudDb
): CoroutineWorker(context,
    workerParams) {

    override suspend fun doWork(): Result {

        if(!userAuth.getIsUserAuthenticated()){
            return Result.retry()
        }

        val notificationToken = inputData.getString(NOTIFICATION_TOKEN)

        return if(notificationToken!=null){

            val task = cloudDb.addDataAsync(notificationToken,
                DbFields.USERS.KEY, userAuth.getUserId(), NOTIFICATION_TOKEN)

            if(task.isSuccessful){
                Result.success()
            }else{
                Result.retry()
            }

        }else{
             Result.success()
        }

    }
}