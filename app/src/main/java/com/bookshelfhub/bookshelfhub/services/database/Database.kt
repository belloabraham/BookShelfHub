package com.bookshelfhub.bookshelfhub.services.database

import android.content.Context
import androidx.work.*
import com.bookshelfhub.bookshelfhub.models.IBookInterest
import com.bookshelfhub.bookshelfhub.services.database.local.LocalDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.BookInterestRecord
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.UserRecord
import com.bookshelfhub.bookshelfhub.workers.UploadBookInterest
import com.bookshelfhub.bookshelfhub.workers.UploadUserData
import com.google.common.base.Optional
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class Database @Inject constructor(private var context: Context, private val localDb: LocalDb) {

    suspend fun addUser(userRecord:UserRecord){
        localDb.addUser(userRecord)

        val oneTimeUserDataUpload: WorkRequest =
            OneTimeWorkRequestBuilder<UploadUserData>()
                .setConstraints(getConnectedConstraint())
                .build()

        val periodicUserDataUpload =
            PeriodicWorkRequestBuilder<UploadUserData>(12, TimeUnit.HOURS)
                .setConstraints(getConnectedConstraint())
                .build()

        WorkManager.getInstance(context).enqueue(oneTimeUserDataUpload)
        WorkManager.getInstance(context).enqueue(periodicUserDataUpload)
    }


    suspend fun addBookInterest(bookInterest: IBookInterest){
        val bookInterestRecord = bookInterest as BookInterestRecord
        localDb.addBookInterest(bookInterestRecord)

        val oneTimeUserDataUpload: WorkRequest =
            OneTimeWorkRequestBuilder<UploadBookInterest>()
                .setConstraints(getConnectedConstraint())
                .build()
        WorkManager.getInstance(context).enqueue(oneTimeUserDataUpload)
    }


    private fun getConnectedConstraint():Constraints{
        return Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
    }

}