package com.bookshelfhub.core.common.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

class NotificationChannelBuilder(
    private val context:Context,
    private val primaryNotificationChannelId:String,
    private val downloadNotificationChannelId:String
    ) {

    fun createNotificationChannels(
        primaryNotificationChannelDesc:String,
        downloadNotificationChannelDesc:String,
        colorRes:Int
    ){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val primaryNotificationChannel = NotificationChannel(
                primaryNotificationChannelId, primaryNotificationChannelDesc, NotificationManager.IMPORTANCE_HIGH
            )
            channelSettings(primaryNotificationChannel, primaryNotificationChannelDesc, colorRes)

            val downloadNotificationChannel = NotificationChannel(
                downloadNotificationChannelId, downloadNotificationChannelDesc, NotificationManager.IMPORTANCE_LOW
            )
            channelSettings(downloadNotificationChannel, downloadNotificationChannelDesc, colorRes)


            val nManager = context.getSystemService(
                NotificationManager::class.java
            )!!
            nManager.createNotificationChannel(primaryNotificationChannel)
            nManager.createNotificationChannel(downloadNotificationChannel)
        }
    }

    private fun channelSettings(nChannel: NotificationChannel, desc: String, colorRes:Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            nChannel.enableLights(true)
            nChannel.lightColor = context.getColor(colorRes)
            nChannel.enableVibration(true)
            nChannel.setShowBadge(true)
            nChannel.description = desc
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                nChannel.setAllowBubbles(true)
            }
        }
    }


}