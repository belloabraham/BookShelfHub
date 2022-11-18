package com.bookshelfhub.core.common.notification

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


class NotificationBuilder(
    private val context:Context,
    private val notificationChannelId:String = context.getString(
    R.string.notification_channel_id),
    private val notificationColor:Int=R.color.notf_color,
    private val smallIcon:Int = R.drawable.ic_stat_bookshelfhub
) {

    private  var uri:String? = null
    private  var actionText:String? = null
    private var largeIcon: Bitmap = IconUtil.getBitmap(context, R.drawable.logo)
    private lateinit var title: String
    private lateinit var message:String
    private var autoCancel = true
    private var onGoing = false
    private var actionIntent:PendingIntent?=null
    private var priority = NotificationCompat.PRIORITY_HIGH
    private var contentIntent:PendingIntent?=null
    private var alertOnlyOnce = true
    private var actionIntentIcon = 0

    private fun getViewPendingIntent(intent:Intent, context: Context): PendingIntent? {
       return  getActivity(context, 0, intent, getIntentFlag())
    }

    /**
     * Defaults to true
     */
    fun setAlertOnlyOnce(value:Boolean):NotificationBuilder{
        alertOnlyOnce = value
        return this
    }

    private fun getActionIntent(): PendingIntent? {
        return  actionIntent
    }

    fun setPriority(value:Int): NotificationBuilder {
        priority = value
        return this
    }

    fun setNotificationProgress(){

    }

    fun setActionIntent(
        pendingIntent: PendingIntent,
        action:String,
        actionIntentIcon:Int = 0
    ): NotificationBuilder {
        this.actionIntent  = pendingIntent
        this.actionIntentIcon = actionIntentIcon
        actionText = action
        return this
    }

    private fun getContentIntent(): PendingIntent? {
        return contentIntent
    }

    fun setContentIntent(pendingIntent: PendingIntent): NotificationBuilder {
        contentIntent  = pendingIntent
        return this
    }

    /**
     * Defaults to false
     */
    fun setOngoing(value: Boolean):NotificationBuilder{
        onGoing = value
        return this
    }
    fun setAutoCancel(value:Boolean): NotificationBuilder {
        autoCancel = value
        return this
    }

    fun setContentText(@StringRes value:Int): NotificationBuilder {
        setContentText(getString(value))
        return this
    }

    fun setContentText(value:String): NotificationBuilder {
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

    fun setUri(@StringRes value:Int): NotificationBuilder {
        setUri(getString(value))
        return this
    }

    fun setUri(value:String): NotificationBuilder {
        this.uri = value
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
            val notificationBuilder = NotificationCompat.Builder(context, notificationChannelId).setSmallIcon(smallIcon)
                    .setLargeIcon(
                        largeIcon
                    )
                    .setContentTitle(title)
                    .setShowWhen(true)
                    .setContentText(message)
                    .setOngoing(onGoing)
                    .setOnlyAlertOnce(alertOnlyOnce)
                    .setStyle( notificationStyle )
                    .setColor(ContextCompat.getColor(context, notificationColor))
                    .setPriority(priority)
                    .setAutoCancel(autoCancel)

            getContentIntent()?.let {
                notificationBuilder.setContentIntent(it)
            }

            getActionIntent()?.let {
                notificationBuilder.addAction(actionIntentIcon, actionText, it)
            }

            return notificationBuilder
        }

        fun showNotification(notificationId:Int){
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(notificationId, getNotificationBuilder().build())
        }

         fun showNotification(notificationId:Int, getViewIntent:String.()->Intent) {
            var viewPendingIntent: PendingIntent? = null
            uri?.let {
                val intent = getViewIntent(it)
                viewPendingIntent = getViewPendingIntent(intent, context)
            }

             val builder = getNotificationBuilder()

             viewPendingIntent?.let {
                builder.addAction(actionIntentIcon, actionText, it)
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