package com.bookshelfhub.core.common.notification

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.PendingIntent.*
import android.content.Context
import com.bookshelfhub.core.resources.R
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.bookshelfhub.core.common.helpers.utils.IconUtil


class
NotificationBuilder(private val context:Context, private val notifChannelId:String = context.getString(
    R.string.notif_channel_id), private val notificationColor:Int=R.color.notf_color, private val smallIcon:Int = R.drawable.ic_stat_bookshelfhub) {

    private  var url:String? = null
    private  var actionText:String? = null
    private var largeIcon: Bitmap = IconUtil.getBitmap(context, R.drawable.logo)
    private lateinit var title: String
    private lateinit var message:String
    private var autoCancel = true
    private var onGoing = false
    private var pendingIntent:PendingIntent?=null
    private var priority = NotificationCompat.PRIORITY_DEFAULT
    private var contentIntent:PendingIntent?=null


    private fun getPendingIntent(intent:Intent, context: Context): PendingIntent? {
        return  getActivity(context, 0, intent, getIntentFlag())
    }

    private fun getPendingIntent(): PendingIntent? {
        return  pendingIntent
    }

    fun setPriority(value:Int): NotificationBuilder {
        priority = value
        return this
    }

    fun setPendingIntent(pendingIntent: PendingIntent, action:String): NotificationBuilder {
        this.pendingIntent  = pendingIntent
        actionText = action
        return this
    }

    private fun getContentIntent(): PendingIntent? {
        return contentIntent
    }

    fun setContentIntent(intent: Intent): NotificationBuilder {
        contentIntent  = getPendingIntent(intent, context)
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

    fun setMessage(@StringRes value:Int): NotificationBuilder {
        setMessage(getString(value))
        return this
    }

    fun setMessage(value:String): NotificationBuilder {
        message = value
        return this
    }


    fun setTitle(@StringRes value:Int): NotificationBuilder {
        setTitle(getString(value))
        return this
    }

    fun setTitle(value:String): NotificationBuilder {
        title = value
        return this
    }

    fun setLargeIcon(@DrawableRes value: Int): NotificationBuilder {
       largeIcon = IconUtil.getBitmap(context, value)
        return this
    }

    fun setLargeIcon(value: Bitmap): NotificationBuilder {
        largeIcon = value
        return this
    }

    fun setActionText(@StringRes value:Int): NotificationBuilder {
        setActionText(getString(value))
        return this
    }

    fun setActionText(value:String): NotificationBuilder {
        this.actionText = value
        return this
    }

    fun setUrl(@StringRes value:Int): NotificationBuilder {
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


    inner class Builder(
       val context: Context,
       private val notificationStyle:NotificationCompat.Style = NotificationCompat.BigTextStyle().bigText(message).setBigContentTitle(title)
       ){

        fun getNotificationBuilder(): NotificationCompat.Builder {
            val notificationBuilder =  NotificationCompat.Builder(context, notifChannelId)
                    .setSmallIcon(smallIcon)
                    .setLargeIcon(
                        largeIcon
                    )
                    .setContentTitle(title)
                    .setShowWhen(true)
                    .setContentText(message)
                    .setOngoing(onGoing)
                    .setStyle(notificationStyle)
                    .setColor(ContextCompat.getColor(context, notificationColor))
                    .setPriority(priority)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setAutoCancel(autoCancel)

            getContentIntent()?.let {
                notificationBuilder.setContentIntent(it)
            }

            getPendingIntent()?.let {
                notificationBuilder.addAction(0, actionText, it)
            }

            return notificationBuilder
        }

        fun showNotification(notificationId:Int){
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(notificationId, getNotificationBuilder().build())
        }

         @SuppressLint("UnspecifiedImmutableFlag")
         fun showNotification(notificationId:Int, getUrlIntent:String.()->Intent) {
            var pendingIntent: PendingIntent? = null
            url?.let {
                val intent = getUrlIntent(it)
                pendingIntent = getPendingIntent(intent, context)
            }

             val builder = getNotificationBuilder()

             pendingIntent?.let {
                builder.addAction(0, actionText, it)
             }

            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(notificationId, builder.build())
        }
    }

    companion object{
         fun getIntentFlag(): Int {
            var flag  = FLAG_UPDATE_CURRENT
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                flag = FLAG_IMMUTABLE or FLAG_UPDATE_CURRENT
            }
            return flag
        }
    }

}