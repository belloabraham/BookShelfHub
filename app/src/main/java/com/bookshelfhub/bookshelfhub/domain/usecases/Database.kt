package com.bookshelfhub.bookshelfhub.domain.usecases

import androidx.work.*
import com.bookshelfhub.bookshelfhub.data.models.entities.BookInterest
import com.bookshelfhub.bookshelfhub.data.models.entities.User
import com.bookshelfhub.bookshelfhub.helpers.database.ILocalDb
import com.bookshelfhub.bookshelfhub.workers.Constraint
import com.bookshelfhub.bookshelfhub.workers.UploadUserData
import com.bookshelfhub.bookshelfhub.workers.Worker
import javax.inject.Inject

class Database @Inject constructor(
    private val localDb: ILocalDb,
    private val worker:Worker
) {

    suspend fun addUser(user: User){

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