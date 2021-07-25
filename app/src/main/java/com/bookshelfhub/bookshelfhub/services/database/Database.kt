package com.bookshelfhub.bookshelfhub.services.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.work.*
import com.bookshelfhub.bookshelfhub.services.database.local.ILocalDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.*
import com.bookshelfhub.bookshelfhub.workers.UploadUserData
import com.google.common.base.Optional
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class Database @Inject constructor(private var context: Context, private val localDb: ILocalDb) {

    suspend fun addUser(user:User){
        localDb.addUser(user)

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

    suspend fun addBookInterest(bookInterest: BookInterest){
        localDb.addBookInterest(bookInterest)
    }

     fun getLiveUser(userId:String): LiveData<User> {
        return  localDb.getLiveUser(userId)
    }

     fun getLiveBookInterest(userId:String): LiveData<Optional<BookInterest>> {
        return  localDb.getLiveBookInterest(userId)
    }

    private fun getConnectedConstraint():Constraints{
        return Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
    }

}