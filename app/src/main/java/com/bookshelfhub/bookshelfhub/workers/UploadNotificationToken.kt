package com.bookshelfhub.bookshelfhub.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bookshelfhub.bookshelfhub.data.repos.UserRepo
import com.bookshelfhub.bookshelfhub.helpers.utils.Logger
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.RemoteDataFields
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.IRemoteDataSource
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.tasks.await

@HiltWorker
class UploadNotificationToken @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val userAuth: IUserAuth,
    private val userRepo: UserRepo,
): CoroutineWorker(context,
    workerParams) {

    override suspend fun doWork(): Result {

        if(!userAuth.getIsUserAuthenticated()){
            return Result.retry()
        }

        val notificationToken = inputData.getString(RemoteDataFields.NOTIFICATION_TOKEN)

        if(notificationToken==null){
            Result.success()
        }

        val userId = userAuth.getUserId()

           return try {
                 userRepo.uploadNotificationToken(notificationToken!!, userId)
                 Result.success()
            }catch (e:Exception){
                Logger.log("Worker:UploadNotifToken", e)
                Result.retry()
            }

    }
}