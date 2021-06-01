package com.bookshelfhub.bookshelfhub.services.notification.firebase

import com.bookshelfhub.bookshelfhub.helpers.notification.NotificationHelper
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.ktx.remoteMessage
import javax.inject.Inject

class FirebaseCMService : FirebaseMessagingService() {

    private val URL="url"
    private val ACTION="action"
    @Inject
    lateinit var notificationHelper: NotificationHelper


    override fun onMessageReceived(cloudMesage: RemoteMessage) {

        if (cloudMesage.data.isNotEmpty()){
            
            //TODO Check if it is payload data before further operation
           // val url= cloudMesage.data.get(URL);
           // val actionText= cloudMesage.data.get(ACTION)

        }
    }

    override fun onNewToken(token: String) {
        //TODO Add token to user data and update to firestore
    }
}