package com.bookshelfhub.bookshelfhub.helpers.notification.firebase

import com.bookshelfhub.bookshelfhub.helpers.notification.ICloudMessaging
import com.google.android.gms.tasks.Task
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
import kotlinx.coroutines.tasks.await

open class CloudMessaging : ICloudMessaging {

  override fun subscribeTo(topic:String){
       Firebase.messaging.subscribeToTopic(topic);
  }

    override fun unsubscribeFrom(topic:String){
        Firebase.messaging.unsubscribeFromTopic(topic)
    }

    override suspend fun getNotificationTokenAsync(): String? {
       return FirebaseMessaging.getInstance().token.await()

    }

}