package com.bookshelfhub.core.common.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

class NotificationChannelBuilder(val context:Context,val primaryNotfChnlId:String) {

    fun createNotificationChannels(primaryNotfChnlDesc:String, colorRes:Int){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val primaryNotifChannel = NotificationChannel(
                primaryNotfChnlId, primaryNotfChnlDesc, NotificationManager.IMPORTANCE_HIGH
            )
            channelSettings(primaryNotifChannel, primaryNotfChnlDesc, colorRes)

            val nManager = context.getSystemService(
                NotificationManager::class.java
            )!!
            nManager.createNotificationChannel(primaryNotifChannel)
        }
    }

    private fun channelSettings(nChannel: NotificationChannel, desc: String, colorRes:Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            nChannel.enableLights(true)
            nChannel.lockscreenVisibility = 0
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