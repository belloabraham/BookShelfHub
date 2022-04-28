package com.bookshelfhub.bookshelfhub.helpers.notification

interface ICloudMessaging {
     fun subscribeTo(topic: String)
     fun unsubscribeFrom(topic: String)
     suspend fun getNotificationTokenAsync(): String?
}