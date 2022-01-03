package com.bookshelfhub.bookshelfhub.services.notification.firebase

import com.bookshelfhub.bookshelfhub.services.notification.ICloudMessaging
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging

open class CloudMessaging : ICloudMessaging {


  override fun subscribeTo(topic:String){
       Firebase.messaging.subscribeToTopic(topic);
   }

    override fun unsubscribeFrom(topic:String){
        Firebase.messaging.unsubscribeFromTopic(topic)
    }

    override fun getNotificationTokenAsync(onComplete:(token:String)->Unit){
        FirebaseMessaging.getInstance()
            .token
            .addOnCompleteListener {
                onComplete(it.result)
            }
    }

}