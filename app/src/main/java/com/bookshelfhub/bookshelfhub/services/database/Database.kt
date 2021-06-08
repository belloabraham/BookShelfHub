package com.bookshelfhub.bookshelfhub.services.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.work.*
import com.bookshelfhub.bookshelfhub.services.database.cloud.CloudDb
import com.bookshelfhub.bookshelfhub.services.database.local.LocalDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.User
import com.bookshelfhub.bookshelfhub.workers.UploadNotificationToken
import com.bookshelfhub.bookshelfhub.workers.UploadUserData
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class Database @Inject constructor(private var context: Context, private val localDb: LocalDb, private val cloudDb:CloudDb) {

    suspend fun addUser(user:User){
        localDb.addUser(user)

        val connected = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val oneTimeUserDataUpload: WorkRequest =
            OneTimeWorkRequestBuilder<UploadUserData>()
                .setConstraints(connected)
                .build()

        val periodicUserDataUpload =
            PeriodicWorkRequestBuilder<UploadUserData>(12, TimeUnit.HOURS)
                .setConstraints(connected)
                .build()

        WorkManager.getInstance(context).enqueue(oneTimeUserDataUpload)
        WorkManager.getInstance(context).enqueue(periodicUserDataUpload)
    }
}