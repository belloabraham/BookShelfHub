package com.bookshelfhub.bookshelfhub.services.notification.firebase

import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.bookshelfhub.bookshelfhub.workers.Constraint
import com.bookshelfhub.bookshelfhub.workers.Tag.NOTIFICATION_TOKEN
import com.bookshelfhub.bookshelfhub.workers.UploadNotificationToken
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

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