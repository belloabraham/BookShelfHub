package com.bookshelfhub.bookshelfhub.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bookshelfhub.core.authentication.IUserAuth
import com.bookshelfhub.core.common.helpers.ErrorUtil
import com.bookshelfhub.core.data.repos.user.IUserRepo
import com.bookshelfhub.core.remote.database.RemoteDataFields
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class UploadNotificationToken @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val userAuth: IUserAuth,
    private val userRepo: IUserRepo,
): CoroutineWorker(context, workerParams) {

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
               ErrorUtil.e(e)
                Result.retry()
            }

    }
}