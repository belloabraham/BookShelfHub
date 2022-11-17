package com.bookshelfhub.bookshelfhub

import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.bookshelfhub.bookshelfhub.workers.UploadNotificationToken
import com.bookshelfhub.core.common.helpers.ErrorUtil
import com.bookshelfhub.core.common.notification.NotificationBuilder
import com.bookshelfhub.core.common.worker.Constraint
import com.bookshelfhub.core.remote.database.RemoteDataFields
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.concurrent.TimeUnit

class CloudMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        //This function will only get called if app is currently in use by the user (in foreground) with data
        //notification message else system tray will handle notification message and pass data to splash activity intent
        //when user taps on notification
        //You have 10 sec to perform any operation here

        if (remoteMessage.data.isNotEmpty()){ }

        if(remoteMessage.notification != null){
            try {
                val notificationId = (3..30).random()
                val message = remoteMessage.notification!!.body!!
                val title = remoteMessage.notification!!.title!!
                NotificationBuilder(applicationContext)
                    .setContentText(message)
                    .setTitle(title)
                    .Builder(applicationContext)
                    .showNotification(notificationId)
            }catch (e:Exception){
                ErrorUtil.e(e)
            }
        }
    }

    override fun onNewToken(token: String) {
        val data = workDataOf(
            RemoteDataFields.NOTIFICATION_TOKEN to token
        )
        val uploadNotificationToken =
            OneTimeWorkRequestBuilder<UploadNotificationToken>()
                .setConstraints(Constraint.getConnected())
                .setInitialDelay(30, TimeUnit.MINUTES)
                .setInputData(data)
                .build()
        WorkManager.getInstance(this).enqueue(uploadNotificationToken)
    }

}