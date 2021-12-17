package com.bookshelfhub.bookshelfhub.services.notification.firebase

import com.bookshelfhub.bookshelfhub.Utils.settings.SettingsUtil
import com.bookshelfhub.bookshelfhub.helpers.notification.NotificationBuilder
import com.bookshelfhub.bookshelfhub.services.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.services.database.cloud.DbFields
import com.bookshelfhub.bookshelfhub.services.database.cloud.ICloudDb
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import javax.inject.Inject

class CloudMessagingService : FirebaseMessagingService() {

    private val URL="url"
    private val ACTION="action"
    @Inject
    lateinit var notificationBuilder: NotificationBuilder
    @Inject
    lateinit var settingsUtil: SettingsUtil
    @Inject
    lateinit var cloudDb: ICloudDb
    @Inject
    lateinit var userAuth: IUserAuth

    private val  NOTIFICATION_TOKEN="notification_token"


    override fun onMessageReceived(cloudMesage: RemoteMessage) {

        if (cloudMesage.data.isNotEmpty()){
            
            //TODO Check if it is payload data before further operation
           // val url= cloudMesage.data.get(URL);
           // val actionText= cloudMesage.data.get(ACTION)

        }
    }


    override fun onNewToken(token: String) {
        cloudDb.addDataAsync(token,
            DbFields.USERS.KEY, userAuth.getUserId(), NOTIFICATION_TOKEN){

        }
    }


}