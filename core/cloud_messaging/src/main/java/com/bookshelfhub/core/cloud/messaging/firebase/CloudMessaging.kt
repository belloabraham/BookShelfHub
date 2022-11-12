package com.bookshelfhub.core.cloud.messaging.firebase

import com.bookshelfhub.core.cloud.messaging.ICloudMessaging
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
import kotlinx.coroutines.tasks.await

open class CloudMessaging : ICloudMessaging {

  override fun subscribeTo(topic:String){
       Firebase.messaging.subscribeToTopic(topic)
  }

    override fun unsubscribeFrom(topic:String){
        Firebase.messaging.unsubscribeFromTopic(topic)
    }

    override suspend fun getNotificationTokenAsync(): String? {
       return FirebaseMessaging.getInstance().token.await()
    }

}