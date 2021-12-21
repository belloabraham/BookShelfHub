package com.bookshelfhub.bookshelfhub.services.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.work.*
import com.bookshelfhub.bookshelfhub.helpers.database.ILocalDb
import com.bookshelfhub.bookshelfhub.helpers.database.room.entities.*
import com.bookshelfhub.bookshelfhub.workers.Constraint
import com.bookshelfhub.bookshelfhub.workers.UploadUserData
import com.google.common.base.Optional
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class Database @Inject constructor(private var context: Context, private val localDb: ILocalDb) {

    suspend fun addUser(user:User){
        localDb.addUser(user)

        val connected = Constraint.getConnected()
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

    suspend fun addBookInterest(bookInterest: BookInterest){
        localDb.addBookInterest(bookInterest)
    }

     fun getLiveUser(userId:String): LiveData<User> {
        return  localDb.getLiveUser(userId)
    }

     fun getLiveBookInterest(userId:String): LiveData<Optional<BookInterest>> {
        return  localDb.getLiveBookInterest(userId)
    }


}