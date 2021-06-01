package com.bookshelfhub.bookshelfhub.services.notification.firebase

import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging

open class FirebaseCM {


  open fun subscribeTo(topic:String){
       Firebase.messaging.subscribeToTopic(topic);
   }

    open fun unsubscribeFrom(topic:String){
        Firebase.messaging.unsubscribeFromTopic(topic)
    }

}