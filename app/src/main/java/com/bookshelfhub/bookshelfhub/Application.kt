package com.bookshelfhub.bookshelfhub

import com.bookshelfhub.bookshelfhub.helpers.notification.NotificationChannelBuilder
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class Application: android.app.Application() {
    private lateinit var notificationChannelBuilder: NotificationChannelBuilder


    override fun onCreate() {
        super.onCreate()
        val  context = applicationContext
        setupFirebaseRemoteConfig()
        notificationChannelBuilder = NotificationChannelBuilder(context,context.getString(R.string.notif_channel_id))
        notificationChannelBuilder.createNotificationChannels(context.getString(R.string.notif_channel_desc),R.color.notf_color)

    }

    private fun setupFirebaseRemoteConfig(){
        val remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 1800
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.firebase_remote_config_defaults)
    }
}