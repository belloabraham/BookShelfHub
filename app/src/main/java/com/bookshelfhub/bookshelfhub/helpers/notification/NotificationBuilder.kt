package com.bookshelfhub.bookshelfhub.helpers.notification

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.PendingIntent.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.Utils.IconUtil

class NotificationBuilder(private val context:Context, private val notifChannelId:String = context.getString(
    R.string.notif_channel_id), private val notificationColor:Int=R.color.notf_color, private val smallIcon:Int = R.drawable.ic_stat_bookshelfhub) {

    private  var url:String? = null
    private  var actionText:String? = null
    private var largeIcon: Bitmap = IconUtil.getBitmap(context, R.drawable.notification_large_icon)
    private lateinit var title: String
    private lateinit var message:String
    private var autoCancel = true
    private var onGoing = false
    private var pendingIntent:PendingIntent?=null
    private var priority = NotificationCompat.PRIORITY_DEFAULT


    private fun getPendingIntent(intent:Intent, context: Context): PendingIntent? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getActivity(context, 0, intent,  FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE)
        } else {
            getActivity(context, 0, intent, FLAG_UPDATE_CURRENT)
        }
    }

    fun setPriority(value:Int): NotificationBuilder {
        priority = value
        return this
    }

    fun setPendingIntent(intent: Intent): NotificationBuilder {
        pendingIntent  = getPendingIntent(intent, context)
        return this
    }

    fun setOngoing(value: Boolean):NotificationBuilder{
        onGoing = value
        return this
    }
    fun setAutoCancel(value:Boolean): NotificationBuilder {
        autoCancel = value
        return this
    }

    fun setMessage(value:Int): NotificationBuilder {
        setMessage(getString(value))
        return this
    }

    fun setMessage(value:String): NotificationBuilder {
        message = value
        return this
    }


    fun setTitle(value:Int): NotificationBuilder {
        setTitle(getString(value))
        return this
    }

    fun setTitle(value:String): NotificationBuilder {
        title = value
        return this
    }

    fun setLargeIcon(value: Int): NotificationBuilder {
       largeIcon = IconUtil.getBitmap(context, value)
        return this
    }

    fun setLargeIcon(value: Bitmap): NotificationBuilder {
        largeIcon = value
        return this
    }

    fun build(notificationStyle:NotificationCompat.Style = NotificationCompat.BigTextStyle().bigText(message).setBigContentTitle(title)): Builder {
        return Builder(this, context, notificationStyle)
    }

    fun setActionText(value:Int): NotificationBuilder {
        setActionText(getString(value))
        return this
    }

    fun setActionText(value:String): NotificationBuilder {
        this.actionText = value
        return this
    }

    fun setUrl(value:Int): NotificationBuilder {
        setUrl(getString(value))
        return this
    }

    fun setUrl(value:String): NotificationBuilder {
        this.url = value
        return this
    }

    private fun getString(value:Int): String {
       return context.getString(value)
    }


    class Builder(private val notificationBuilder: NotificationBuilder, val context: Context, private val notificationStyle:NotificationCompat.Style){


        fun getNotificationBuiler(): NotificationCompat.Builder {
            return NotificationCompat.Builder(context, notificationBuilder.notifChannelId)
                    .setSmallIcon(notificationBuilder.smallIcon)
                    .setLargeIcon(
                        notificationBuilder.largeIcon
                    )
                    .setContentTitle(notificationBuilder.title)
                    .setShowWhen(true)
                    .setContentText(notificationBuilder.message)
                    .setOngoing(notificationBuilder.onGoing)
                    .setContentIntent(notificationBuilder.pendingIntent)
                    .setStyle(notificationStyle)
                    .setColor(ContextCompat.getColor(context, notificationBuilder.notificationColor))
                    .setPriority(notificationBuilder.priority)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setAutoCancel(notificationBuilder.autoCancel)
        }

        fun showNotification(notificationId:Int){
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(notificationId, getNotificationBuiler().build())
        }

         @SuppressLint("UnspecifiedImmutableFlag")
         fun showNotification(notificationId:Int, getUrlIntent:String.()->Intent) {
            var pendingIntent: PendingIntent? = null
            notificationBuilder.url?.let {
                val intent = getUrlIntent(it)
                intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP

                pendingIntent = notificationBuilder.getPendingIntent(intent, context)
            }

             val builder = getNotificationBuiler()

             pendingIntent?.let {
                builder.addAction(0, notificationBuilder.actionText, it)
             }

            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(notificationId, builder.build())
        }

    }
}