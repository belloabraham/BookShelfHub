package com.bookshelfhub.bookshelfhub

import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import com.bookshelfhub.bookshelfhub.workers.*
import com.bookshelfhub.core.common.helpers.ErrorUtil
import com.bookshelfhub.core.common.helpers.utils.datetime.DateTimeUtil
import com.bookshelfhub.core.common.notification.NotificationChannelBuilder
import com.bookshelfhub.core.common.worker.Constraint
import com.bookshelfhub.core.common.worker.Tag
import com.bookshelfhub.core.common.worker.Worker
import com.bookshelfhub.core.remote.remote_config.RemoteConfig
import com.bookshelfhub.feature.book_reviews.workers.UpdatePreviouslyPostedUnverifiedUserReview
import com.bookshelfhub.bookshelfhub.workers.DeleteBookmarks
import com.bookshelfhub.payment.PaymentSDK
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@HiltAndroidApp
class Application: android.app.Application(), Configuration.Provider {

    // ***Dagger Hilt Worker ***//
    @Inject
    lateinit var workerFactory: HiltWorkerFactory
    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    @Inject
    lateinit var worker: Worker

    override fun onCreate() {
        super.onCreate()

        // Should come first
        setupFirebaseAppCheck()

        if (Config.isDevMode()) {
            ErrorUtil.initialize()
        }

        DateTimeUtil.initialize(applicationContext)

        RemoteConfig.initialize()

        PaymentSDK.initialize(applicationContext)

        NotificationChannelBuilder(
            this,
            getString(R.string.notification_channel_id),
            getString(R.string.download_channel_id)
        ).createNotificationChannels(
            getString(R.string.notification_channel_desc),
            getString(R.string.download_channel_desc),
            R.color.notf_color
        )

        enqueueWorkers()
    }

    private fun enqueueWorkers(){
        val connected = Constraint.getConnected()

        val removeUnPublishedBooks = PeriodicWorkRequestBuilder<UnPublishedBooks>(repeatInterval = 6, TimeUnit.HOURS)
            .setInitialDelay(1, TimeUnit.HOURS)
            .setConstraints(connected)
            .build()

        val updatePublishedBooks = PeriodicWorkRequestBuilder<UpdatePublishedBooks>(repeatInterval = 10, TimeUnit.DAYS)
            .setInitialDelay( 1, TimeUnit.HOURS)
            .setConstraints(connected)
            .build()

        val deleteBookmarks = PeriodicWorkRequestBuilder<DeleteBookmarks>(repeatInterval = 12, TimeUnit.HOURS)
            .setConstraints(connected)
            .build()

        val postPendingUserReview = PeriodicWorkRequestBuilder<UpdatePreviouslyPostedUnverifiedUserReview>(repeatInterval = 12, TimeUnit.HOURS)
            .setConstraints(connected)
            .build()


       enqueueUniquePeriodicWork(Tag.postPendingUserReview, postPendingUserReview)
       enqueueUniquePeriodicWork(Tag.removeUnPublishedBooks,removeUnPublishedBooks)
       enqueueUniquePeriodicWork(Tag.updatePublishedBooks, updatePublishedBooks)
       enqueueUniquePeriodicWork(Tag.deleteBookmarks, deleteBookmarks)
    }


    private fun enqueueUniquePeriodicWork(tag:String,workRequest: PeriodicWorkRequest,  workPolicy:ExistingPeriodicWorkPolicy=ExistingPeriodicWorkPolicy.KEEP){
       worker.enqueueUniquePeriodicWork(tag, workRequest, workPolicy)
    }

    private fun setupFirebaseAppCheck(){
        // ***Initialize firebase (Required by App Check)***//
        FirebaseApp.initializeApp(this)

        // ***Setup App Check ***//
        val firebaseAppCheck = FirebaseAppCheck.getInstance()

        if(Config.isDevMode()){
            firebaseAppCheck.installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance()
            )
        }else{
            firebaseAppCheck.installAppCheckProviderFactory(
                SafetyNetAppCheckProviderFactory.getInstance())
        }
    }

}