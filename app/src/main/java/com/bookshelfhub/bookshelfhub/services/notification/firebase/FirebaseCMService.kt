package com.bookshelfhub.bookshelfhub.services.notification.firebase

import com.bookshelfhub.bookshelfhub.Utils.SettingsUtil
import com.bookshelfhub.bookshelfhub.enums.Settings
import com.bookshelfhub.bookshelfhub.helpers.notification.NotificationHelper
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.ktx.remoteMessage
import kotlinx.coroutines.runBlocking
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
        runBlocking {
            settingsUtil.setString(Settings.NOTIF_TOKEN.KEY, token)
        }
        //TODO Start a one time request worker that requires internet connection that saves token to the cloud using user id
        //Todo margin the data with the existing one
    }
}