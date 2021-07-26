package com.bookshelfhub.bookshelfhub

import androidx.hilt.work.HiltWorkerFactory
import androidx.work.*
import com.bookshelfhub.bookshelfhub.helpers.notification.NotificationChannelBuilder
import com.bookshelfhub.bookshelfhub.workers.*
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@HiltAndroidApp
class Application: android.app.Application(), Configuration.Provider {
    private lateinit var notificationChannelBuilder: NotificationChannelBuilder
    private val rmcFetchIntervalInSeconds = 1800L
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        //Initialize firebase (Required for App Check)
        FirebaseApp.initializeApp(this)

        //Setup App Check
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            SafetyNetAppCheckProviderFactory.getInstance())

        AndroidThreeTen.init(this)

        setupFirebaseRemoteConfig()
        
        notificationChannelBuilder = NotificationChannelBuilder(this,getString(R.string.notif_channel_id))
        notificationChannelBuilder.createNotificationChannels(getString(R.string.notif_channel_desc),R.color.notf_color)

         enqueueWorkers()
    }


    private fun enqueueWorkers(){
        val connected = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val removeUnPublishedBooks = PeriodicWorkRequestBuilder<UnPublishedBooks>(6, TimeUnit.HOURS)
                .setInitialDelay(1, TimeUnit.HOURS)
                .setConstraints(connected)
                .build()

        val updatePublishedBooks = PeriodicWorkRequestBuilder<UpdatePublishedBooks>(23, TimeUnit.HOURS)
            .setInitialDelay(1, TimeUnit.HOURS)
            .setConstraints(connected)
            .build()

        val oneTimeNotificationTokenUpload =
            OneTimeWorkRequestBuilder<UploadNotificationToken>()
                .setConstraints(connected)
                .build()

        WorkManager.getInstance(applicationContext).enqueue(oneTimeNotificationTokenUpload)
        WorkManager.getInstance(applicationContext).enqueue(removeUnPublishedBooks)
        WorkManager.getInstance(applicationContext).enqueue(updatePublishedBooks)
    }


    private fun setupFirebaseRemoteConfig(){
        val remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            this.minimumFetchIntervalInSeconds = rmcFetchIntervalInSeconds
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.firebase_remote_config_defaults)
    }
}