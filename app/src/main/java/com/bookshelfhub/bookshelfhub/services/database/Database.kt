package com.bookshelfhub.bookshelfhub.services.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.work.*
import com.bookshelfhub.bookshelfhub.helpers.database.ILocalDb
import com.bookshelfhub.bookshelfhub.helpers.database.room.entities.*
import com.bookshelfhub.bookshelfhub.workers.Constraint
import com.bookshelfhub.bookshelfhub.workers.Tag
import com.bookshelfhub.bookshelfhub.workers.UploadUserData
import com.bookshelfhub.bookshelfhub.workers.Worker
import com.google.common.base.Optional
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class Database @Inject constructor(
    private val localDb: ILocalDb,
    private val worker:Worker
) {

    suspend fun addUser(user:User){

        localDb.addUser(user)

        val connected = Constraint.getConnected()
        val oneTimeUserDataUpload =
            OneTimeWorkRequestBuilder<UploadUserData>()
                .setConstraints(connected)
                .build()

        worker.enqueue(oneTimeUserDataUpload)
    }

    suspend fun addBookInterest(bookInterest: BookInterest){
        localDb.addBookInterest(bookInterest)
    }

}