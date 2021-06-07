package com.bookshelfhub.bookshelfhub.services.notification.firebase

import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
import kotlin.concurrent.timerTask

open class FirebaseCM {


  open fun subscribeTo(topic:String){
       Firebase.messaging.subscribeToTopic(topic);
   }

    open fun unsubscribeFrom(topic:String){
        Firebase.messaging.unsubscribeFromTopic(topic)
    }

    open fun  getNotificationTokenAsync(onComplete:(token:String)->Unit){
        FirebaseMessaging.getInstance()
            .token
            .addOnCompleteListener {
                onComplete(it.result)
            }
    }

}