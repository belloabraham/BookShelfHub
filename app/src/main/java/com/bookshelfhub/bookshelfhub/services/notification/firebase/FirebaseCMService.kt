package com.bookshelfhub.bookshelfhub.services.notification.firebase

import androidx.work.*
import com.bookshelfhub.bookshelfhub.Utils.SettingsUtil
import com.bookshelfhub.bookshelfhub.helpers.notification.NotificationHelper
import com.bookshelfhub.bookshelfhub.workers.UploadNotificationToken
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class FirebaseCMService : FirebaseMessagingService() {

    private val URL="url"
    private val ACTION="action"
    @Inject
    lateinit var notificationHelper: NotificationHelper
    @Inject
    lateinit var settingsUtil: SettingsUtil

    override fun onMessageReceived(cloudMesage: RemoteMessage) {

        if (cloudMesage.data.isNotEmpty()){
            
            //TODO Check if it is payload data before further operation
           // val url= cloudMesage.data.get(URL);
           // val actionText= cloudMesage.data.get(ACTION)

        }
    }

    override fun onNewToken(token: String) {

        val connected = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val oneTimeNotificationTokenUpload: WorkRequest =
            OneTimeWorkRequestBuilder<UploadNotificationToken>()
                .setConstraints(connected)
                .build()

        val periodicNotificationTokenUpload =
            PeriodicWorkRequestBuilder<UploadNotificationToken>(30, TimeUnit.MINUTES)
                .setConstraints(connected)
                .build()

        WorkManager.getInstance(applicationContext).enqueue(oneTimeNotificationTokenUpload)
        WorkManager.getInstance(applicationContext).enqueue(periodicNotificationTokenUpload)

    }
}