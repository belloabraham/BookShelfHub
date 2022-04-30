package com.bookshelfhub.bookshelfhub.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.data.repos.user.IUserRepo
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.helpers.notification.ICloudMessaging
import com.bookshelfhub.bookshelfhub.helpers.notification.NotificationBuilder
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber

@HiltWorker
class UploadUserData  @AssistedInject constructor (
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val userRepo: IUserRepo,
    private val userAuth:IUserAuth,
    private val cloudMessaging: ICloudMessaging
    ): CoroutineWorker(context,
    workerParams
) {

    override suspend fun doWork(): Result {

        val userId = userAuth.getUserId()
        val user = userRepo.getUser(userId)

        val userData = user.get()

        if(user.isPresent){
            Result.success()
        }

       return  try {
                userData.gender?.let {
                    cloudMessaging.subscribeTo(it.lowercase())
                }
                 userRepo.uploadUser(userData, userId)
                 userData.uploaded = true
                 userRepo.addUser(userData)

           val userAddedAdditionalInfo = userData.additionInfo != null
           if(userAddedAdditionalInfo){
               val notificationId = (4..40).random()
               NotificationBuilder(applicationContext)
                   .setTitle(R.string.profile_updated)
                   .setMessage(R.string.profile_updated_msg)
                   .Builder(applicationContext)
                   .showNotification(notificationId)
           }

                 Result.success()
         }catch (e:Exception){
           Timber.e(e)
             Result.retry()
         }


    }
}