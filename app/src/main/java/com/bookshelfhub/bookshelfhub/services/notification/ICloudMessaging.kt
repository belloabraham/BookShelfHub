package com.bookshelfhub.bookshelfhub.services.notification

interface ICloudMessaging {
     fun subscribeTo(topic: String)
     fun unsubscribeFrom(topic: String)
     fun getNotificationTokenAsync(onComplete: (token: String) -> Unit)
}