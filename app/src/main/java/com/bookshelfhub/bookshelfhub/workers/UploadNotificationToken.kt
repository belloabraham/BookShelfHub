package com.bookshelfhub.bookshelfhub.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.bookshelfhub.bookshelfhub.Utils.SettingsUtil
import com.bookshelfhub.bookshelfhub.enums.DbCollections
import com.bookshelfhub.bookshelfhub.services.authentication.UserAuth
import com.bookshelfhub.bookshelfhub.services.database.cloud.CloudDb
import com.bookshelfhub.bookshelfhub.services.notification.CloudMessaging
import kotlinx.coroutines.runBlocking

class UploadNotificationToken (var context: Context, workerParams: WorkerParameters): Worker(context,
    workerParams
) {
    private lateinit var userAuth: UserAuth
    private lateinit var cloudDb: CloudDb
    private lateinit var cloudMessaging: CloudMessaging
    private lateinit var settingsUtil: SettingsUtil
    private val  notificationTokenKey="notification_token"

    override fun doWork(): Result {
        userAuth=UserAuth()
        cloudDb= CloudDb()
        cloudMessaging= CloudMessaging()
        settingsUtil= SettingsUtil(context)


        val appToken:String?
        runBlocking {
            appToken = settingsUtil.getString(notificationTokenKey)
        }

        cloudMessaging.getNotificationTokenAsync {
            val newToken=it
           if (appToken!=newToken){
               val token = hashMapOf(
                   notificationTokenKey to newToken
               )
               cloudDb.addDataAsync(token,DbCollections.USERS.KEY, userAuth.getUserId()) {
                   runBlocking {
                       settingsUtil.setString(notificationTokenKey, newToken)
                   }
               }
           }

        }

        return Result.success()
    }
}