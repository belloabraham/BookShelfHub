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
import androidx.emoji.text.EmojiCompat
import androidx.emoji.text.FontRequestEmojiCompatConfig
import androidx.core.provider.FontRequest
import co.paystack.android.PaystackSdk
import com.bookshelfhub.bookshelfhub.config.RemoteConfig

@HiltAndroidApp
class Application: android.app.Application(), Configuration.Provider {

    //***Required By Dagger Hilt ***//
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    //***Dagger Hilt Configuration ***//
    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()


        //***Should come First ***//
        setUpAppCheck()

        //***Required by Paystack SDK ***//
        PaystackSdk.initialize(applicationContext);

        setupDownloadableEmojiFont()

        //***Joda Time Library Initialization ***//
        AndroidThreeTen.init(this)

        setupFirebaseRemoteConfig()

        //***Creating Notification Channel required by Android 8+ ***//
        NotificationChannelBuilder(this,getString(R.string.notif_channel_id))
            .createNotificationChannels(getString(R.string.notif_channel_desc),R.color.notf_color)

        //***Enqueue WorkManager workers ***//
        enqueueWorkers()
    }


    private fun enqueueWorkers(){

        val connected = Constraint.getConnected()
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

    private  fun setupDownloadableEmojiFont(){
        val fontRequest = FontRequest(
            EmojiFont.PROVIDER_AUTHORITY.VALUE,
            EmojiFont.PROVIDER_PACKAGE.VALUE,
            EmojiFont.QUERY.VALUE,
            R.array.com_google_android_gms_fonts_certs)
       val config = FontRequestEmojiCompatConfig(applicationContext, fontRequest)
            .setReplaceAll(true)
        EmojiCompat.init(config)
    }

    private fun setUpAppCheck(){
        //***Initialize firebase (Required by App Check)***//
        FirebaseApp.initializeApp(this)

        //***Setup App Check ***//
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            SafetyNetAppCheckProviderFactory.getInstance())
    }

    private fun setupFirebaseRemoteConfig(){
        val remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            this.minimumFetchIntervalInSeconds = RemoteConfig.FETCH_INTERVAL_IN_SEC.VALUE
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.firebase_remote_config_defaults)
    }
}