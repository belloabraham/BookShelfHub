package com.bookshelfhub.bookshelfhub

import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import com.bookshelfhub.bookshelfhub.helpers.notification.NotificationBuilder

class BookDownloadService : LifecycleService() {


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startInForeground(){
        val notification = NotificationBuilder(this)
        notification
            .setActionText("")
            .setAutoCancel(false)
            .setTitle("")
            .setOngoing(true)
            .setMessage("")
            .setTitle("")
            .setPriority(NotificationCompat.PRIORITY_LOW)
        startForeground(0, notification.build().getNotificationBuiler().build())
    }

}