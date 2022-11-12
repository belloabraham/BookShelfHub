package com.bookshelfhub.core.cloud.messaging

interface ICloudMessaging {
     fun subscribeTo(topic: String)
     fun unsubscribeFrom(topic: String)
     suspend fun getNotificationTokenAsync(): String?
}