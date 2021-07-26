package com.bookshelfhub.bookshelfhub.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bookshelfhub.bookshelfhub.Utils.SettingsUtil
import com.bookshelfhub.bookshelfhub.enums.DbFields
import com.bookshelfhub.bookshelfhub.services.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.services.authentication.firebase.FBUserAuth
import com.bookshelfhub.bookshelfhub.services.database.cloud.Firestore
import com.bookshelfhub.bookshelfhub.services.database.cloud.ICloudDb
import com.bookshelfhub.bookshelfhub.services.notification.firebase.FirebaseCM
import com.bookshelfhub.bookshelfhub.services.notification.firebase.ICloudMessaging
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.coroutineScope

@HiltWorker
class UploadNotificationToken @AssistedInject constructor (
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val userAuth:IUserAuth, private val cloudDb:ICloudDb,
    private val settingsUtil: SettingsUtil,
    private val cloudMessaging:ICloudMessaging): CoroutineWorker(context,
    workerParams
) {
    private val  NOTIFICATION_TOKEN="notification_token"

    override suspend fun doWork(): Result {


        if (!userAuth.getIsUserAuthenticated()){
            return Result.retry()
        }


        val appToken = settingsUtil.getString(NOTIFICATION_TOKEN)

        cloudMessaging.getNotificationTokenAsync {
            val newToken= it
           if (appToken!=newToken){
               cloudDb.addDataAsync(newToken,DbFields.USERS_COLL.KEY, userAuth.getUserId(), NOTIFICATION_TOKEN) {
                   coroutineScope {
                       settingsUtil.setString(NOTIFICATION_TOKEN, newToken)
                   }
               }
           }

        }

        return Result.success()
    }
}