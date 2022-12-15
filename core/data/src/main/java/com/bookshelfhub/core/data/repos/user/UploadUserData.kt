package com.bookshelfhub.core.data.repos.user

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bookshelfhub.core.authentication.IUserAuth
import com.bookshelfhub.core.cloud.messaging.ICloudMessaging
import com.bookshelfhub.core.common.helpers.ErrorUtil
import com.bookshelfhub.core.common.notification.NotificationBuilder
import dagger.assisted.Assisted
import com.bookshelfhub.core.remote.R
import dagger.assisted.AssistedInject

@HiltWorker
class UploadUserData  @AssistedInject constructor (
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val userRepo: IUserRepo,
    private val userAuth: IUserAuth,
    private val cloudMessaging: ICloudMessaging
    ): CoroutineWorker(context,
    workerParams
) {

    override suspend fun doWork(): Result {

        val userId = userAuth.getUserId()
        val user = userRepo.getUser(userId)

        if(!user.isPresent){
            Result.success()
        }

        val userData = user.get()

       return  try {
                userData.gender?.let {
                    cloudMessaging.subscribeTo(it.lowercase())
                }
                 userRepo.uploadUser(userData, userId)
                 userData.uploaded = true
                 userRepo.addUser(userData)

               val notificationId = (4..40).random()
               NotificationBuilder(applicationContext)
                   .setTitle(R.string.profile_updated)
                   .setContentText(R.string.profile_updated_msg)
                   .Builder(applicationContext)
                   .showNotification(notificationId)

           Result.success()
         }catch (e:Exception){
           ErrorUtil.e(e)
           Result.retry()
         }


    }
}