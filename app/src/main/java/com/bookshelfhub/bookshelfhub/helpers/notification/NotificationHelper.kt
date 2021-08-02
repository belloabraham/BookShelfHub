package com.bookshelfhub.bookshelfhub.helpers.notification

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.Utils.IconUtil

class NotificationHelper(private val context:Context, private val notifChannelId:String = context.getString(
    R.string.notif_channel_id) , private val notificationColor:Int=R.color.notf_color, private val smallIcon:Int = R.drawable.ic_stat_bookshelfhub) {

    private  var url:String? = null
    private  var actionText:String? = null
    private var largeIcon: Bitmap = IconUtil.getBitmap(context, R.drawable.notification_large_icon)
    private lateinit var title: String
    private lateinit var message:String
    private var autoCancel = true

    fun setAutoCancel(value:Boolean): NotificationHelper {
        autoCancel = value
        return this
    }

    fun setMessage(value:Int): NotificationHelper {
        setMessage(getString(value))
        return this
    }

    fun setMessage(value:String): NotificationHelper {
        message = value
        return this
    }


    fun setTitle(value:Int): NotificationHelper {
        setTitle(getString(value))
        return this
    }

    fun setTitle(value:String): NotificationHelper {
        title = value
        return this
    }

    fun setLargeIcon(value: Int): NotificationHelper {
       largeIcon = IconUtil.getBitmap(context, value)
        return this
    }

    fun setLargeIcon(value: Bitmap): NotificationHelper {
        largeIcon = value
        return this
    }

    fun build(notificationStyle:NotificationCompat.Style = NotificationCompat.BigTextStyle().bigText(message).setBigContentTitle(title)): Builder {
        return Builder(this, context, notificationStyle)
    }

    fun setActionText(value:Int): NotificationHelper {
        setActionText(getString(value))
        return this
    }

    fun setActionText(value:String): NotificationHelper {
        this.actionText = value
        return this
    }

    fun setUrl(value:Int): NotificationHelper {
        setUrl(getString(value))
        return this
    }

    fun setUrl(value:String): NotificationHelper {
        this.url = value
        return this
    }

    private fun getString(value:Int): String {
       return context.getString(value)
    }


    class Builder(private val notificationHelper: NotificationHelper, val context: Context, private val notificationStyle:NotificationCompat.Style){


         fun showNotification(notificationId:Int, getUrlIntent:String.()->Intent) {
            var pendingIntent: PendingIntent? = null
            notificationHelper.url?.let {
                val intent = getUrlIntent(it)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
            }

             val msg = notificationHelper.message
             val title = notificationHelper.title
             val builder: NotificationCompat.Builder =
                    NotificationCompat.Builder(context, notificationHelper.notifChannelId)
                    .setSmallIcon(notificationHelper.smallIcon)
                    .setLargeIcon(
                        notificationHelper.largeIcon
                    )
                    .setContentTitle(title)
                    .setShowWhen(true)
                    .setContentText(msg)
                    .setStyle(
                        notificationStyle
                    )
                    .setColor(ContextCompat.getColor(context, notificationHelper.notificationColor))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setAutoCancel(notificationHelper.autoCancel)

             pendingIntent?.let {
                builder.addAction(0, notificationHelper.actionText, it)
             }

            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(notificationId, builder.build())
        }

    }
}