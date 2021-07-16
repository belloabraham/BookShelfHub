package com.bookshelfhub.bookshelfhub.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.bookshelfhub.bookshelfhub.Utils.SettingsUtil
import com.bookshelfhub.bookshelfhub.Utils.StringUtil
import com.bookshelfhub.bookshelfhub.enums.DbFields
import com.bookshelfhub.bookshelfhub.services.authentication.UserAuth
import com.bookshelfhub.bookshelfhub.services.database.cloud.CloudDb
import com.bookshelfhub.bookshelfhub.services.notification.CloudMessaging
import kotlinx.coroutines.runBlocking

class UploadNotificationToken ( val context: Context, workerParams: WorkerParameters): CoroutineWorker(context,
    workerParams
) {
    private lateinit var userAuth: UserAuth
    private lateinit var cloudDb: CloudDb
    private lateinit var cloudMessaging: CloudMessaging
    private lateinit var settingsUtil: SettingsUtil
    private lateinit var stringUtil: StringUtil
    private val  NOTIFICATION_TOKEN="notification_token"

    override suspend fun doWork(): Result {
        stringUtil = StringUtil()
        userAuth=UserAuth(stringUtil)

        if (!userAuth.getIsUserAuthenticated()){
            return Result.retry()
        }

        cloudDb= CloudDb()
        cloudMessaging= CloudMessaging()
        settingsUtil= SettingsUtil(context)


        val appToken = settingsUtil.getString(NOTIFICATION_TOKEN)

        cloudMessaging.getNotificationTokenAsync {
            val newToken=it
           if (appToken!=newToken){
               cloudDb.addDataAsync(newToken,DbFields.USERS_COLL.KEY, userAuth.getUserId(), NOTIFICATION_TOKEN) {
                   runBlocking {
                       settingsUtil.setString(NOTIFICATION_TOKEN, newToken)
                   }
               }
           }

        }

        return Result.success()
    }
}