package com.bookshelfhub.bookshelfhub.helpers.notification

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.bookshelfhub.bookshelfhub.Utils.IntentUtil

class NotificationHelper(private val context:Context, private val notfChannelId:String, private val intentUtil: IntentUtil, private val notifColor:Int, private val smallIcon:Int, private val largeIcon:Int) {

    fun showNotification(title: String, msg: String, notifId:Int, url:String?, actionText:String?){
        if (url==null){
            startNotification(title,msg, notifId,null, null)
        }else{
            val intent: Intent = if (url.startsWith("com."))
                    intentUtil.openAppStoreIntent(url)
                else intentUtil.intent(url)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
            startNotification(title,msg, notifId, actionText, pendingIntent)
        }
    }

   private fun startNotification(title: String, msg: String, notifId:Int, actionText:String?, pendingIntent: PendingIntent?) {
        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(context, notfChannelId)
                .setSmallIcon(smallIcon)
                .setLargeIcon(
                    BitmapFactory.decodeResource(
                        context.getResources(),
                        largeIcon
                    )
                )
                .setContentTitle(title)
                .setShowWhen(true)
                .setContentText(msg)
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(msg).setBigContentTitle(title)
                )
                .setColor(ContextCompat.getColor(context, notifColor))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(true)

        if (pendingIntent!=null){
            builder.addAction(0,actionText,pendingIntent)
        }

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(notifId, builder.build())
    }
}