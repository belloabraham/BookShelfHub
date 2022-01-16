package com.bookshelfhub.bookshelfhub.services.notification.firebase

import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.bookshelfhub.bookshelfhub.Utils.settings.SettingsUtil
import com.bookshelfhub.bookshelfhub.helpers.notification.NotificationBuilder
import com.bookshelfhub.bookshelfhub.services.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.workers.Constraint
import com.bookshelfhub.bookshelfhub.workers.Tag.NOTIFICATION_TOKEN
import com.bookshelfhub.bookshelfhub.workers.UploadNotificationToken
import com.bookshelfhub.bookshelfhub.workers.Worker
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import javax.inject.Inject

class CloudMessagingService : FirebaseMessagingService() {

    private val URL="url"
    private val ACTION="action"


    override fun onMessageReceived(cloudMesage: RemoteMessage) {

        if (cloudMesage.data.isNotEmpty()){
            
            //TODO Check if it is payload data before further operation
           // val url= cloudMesage.data.get(URL);
           // val actionText= cloudMesage.data.get(ACTION)

        }
    }

    override fun onNewToken(token: String) {

        val data = Data.Builder()
        data.putString(NOTIFICATION_TOKEN, token)

        val uploadNotificationToken =
            OneTimeWorkRequestBuilder<UploadNotificationToken>()
                .setConstraints(Constraint.getConnected())
                .setInputData(data.build())
                .build()
        WorkManager.getInstance(this).enqueue(uploadNotificationToken)

    }


}