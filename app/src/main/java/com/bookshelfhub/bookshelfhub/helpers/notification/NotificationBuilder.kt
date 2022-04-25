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
import com.bookshelfhub.bookshelfhub.helpers.utils.IconUtil

class NotificationBuilder(private val context:Context, private val notifChannelId:String = context.getString(
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


     fun getPendingIntent(intent:Intent, context: Context): PendingIntent? {

        return  getActivity(context, 0, intent, getIntentFlag())

    }

     fun getPendingIntent(): PendingIntent? {
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

    fun getContentIntent(): PendingIntent? {
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


   inner class Builder(
       val context: Context,
       private val notificationStyle:NotificationCompat.Style = NotificationCompat.BigTextStyle().bigText(message).setBigContentTitle(title)
       ){

        private val mNotificationBuilder = this@NotificationBuilder

        fun getNotificationBuilder(): NotificationCompat.Builder {
            val notificationBuilder =  NotificationCompat.Builder(context, mNotificationBuilder.notifChannelId)
                    .setSmallIcon(mNotificationBuilder.smallIcon)
                    .setLargeIcon(
                        mNotificationBuilder.largeIcon
                    )
                    .setContentTitle(mNotificationBuilder.title)
                    .setShowWhen(true)
                    .setContentText(mNotificationBuilder.message)
                    .setOngoing(mNotificationBuilder.onGoing)
                    .setStyle(notificationStyle)
                    .setColor(ContextCompat.getColor(context, mNotificationBuilder.notificationColor))
                    .setPriority(mNotificationBuilder.priority)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setAutoCancel(mNotificationBuilder.autoCancel)

            mNotificationBuilder.getContentIntent()?.let {
                notificationBuilder.setContentIntent(it)
            }

            mNotificationBuilder.getPendingIntent()?.let {
                notificationBuilder.addAction(0, mNotificationBuilder.actionText, it)
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
            mNotificationBuilder.url?.let {
                val intent = getUrlIntent(it)
                pendingIntent = mNotificationBuilder.getPendingIntent(intent, context)
            }

             val builder = getNotificationBuilder()

             pendingIntent?.let {
                builder.addAction(0, mNotificationBuilder.actionText, it)
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