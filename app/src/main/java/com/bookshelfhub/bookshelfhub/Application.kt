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
import co.paystack.android.PaystackSdk

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

        /**
         * Should come first
         */
        setUpAppCheck()


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
        val removeUnPublishedBooks = PeriodicWorkRequestBuilder<UnPublishedBooks>(repeatInterval = 6, TimeUnit.HOURS)
            .setInitialDelay(1, TimeUnit.HOURS)
            .setConstraints(connected)
            .build()

        val updatePublishedBooks = PeriodicWorkRequestBuilder<UpdatePublishedBooks>(repeatInterval = 7, TimeUnit.DAYS)
            .setInitialDelay( 1, TimeUnit.HOURS)
            .setConstraints(connected)
            .build()

        val deleteBookmarks = PeriodicWorkRequestBuilder<DeleteBookmarks>(repeatInterval = 12, TimeUnit.HOURS)
            .setConstraints(connected)
            .build()

        val postPendingUserReview = PeriodicWorkRequestBuilder<PostPendingUserReview>(repeatInterval = 12, TimeUnit.HOURS)
            .setConstraints(connected)
            .build()


       enqueueUniquePeriodicWork(Tag.postPendingUserReview, postPendingUserReview)
       enqueueUniquePeriodicWork(Tag.removeUnPublishedBooks,removeUnPublishedBooks)
       enqueueUniquePeriodicWork(Tag.updatePublishedBooks, updatePublishedBooks)
       enqueueUniquePeriodicWork(Tag.deleteBookmarks, deleteBookmarks)
    }


    private fun enqueueUniquePeriodicWork(tag:String,workRequest: PeriodicWorkRequest,  workPolicy:ExistingPeriodicWorkPolicy=ExistingPeriodicWorkPolicy.KEEP){
        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(tag, workPolicy, workRequest)
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

        if(BuildConfig.DEBUG){
            val configSettings = remoteConfigSettings {
                this.minimumFetchIntervalInSeconds = 1800L
            }
            remoteConfig.setConfigSettingsAsync(configSettings)
        }

        remoteConfig.setDefaultsAsync(R.xml.firebase_remote_config_defaults)
    }
}