package com.bookshelfhub.bookshelfhub.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bookshelfhub.bookshelfhub.helpers.utils.Logger
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.RemoteDataFields
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.IRemoteDataSource
import com.bookshelfhub.bookshelfhub.workers.Tag.NOTIFICATION_TOKEN
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.tasks.await

@HiltWorker
class UploadNotificationToken @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val userAuth: IUserAuth,
    private val remoteDataSource: IRemoteDataSource
): CoroutineWorker(context,
    workerParams) {

    override suspend fun doWork(): Result {

        if(!userAuth.getIsUserAuthenticated()){
            return Result.retry()
        }

        val notificationToken = inputData.getString(NOTIFICATION_TOKEN)

        return if(notificationToken!=null){

            try {
                val task = remoteDataSource.addDataAsync(notificationToken,
                    RemoteDataFields.USERS_COLL, userAuth.getUserId(), NOTIFICATION_TOKEN).await()

                task.run {
                    Result.success()
                }
            }catch (e:Exception){
                Logger.log("Worker:UploadNotifToken", e)
                Result.retry()
            }

        }else{
             Result.success()
        }

    }
}