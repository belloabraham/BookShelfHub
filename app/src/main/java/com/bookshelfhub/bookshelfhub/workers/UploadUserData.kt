package com.bookshelfhub.bookshelfhub.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.bookshelfhub.bookshelfhub.Utils.StringUtil
import com.bookshelfhub.bookshelfhub.enums.DbFields
import com.bookshelfhub.bookshelfhub.services.authentication.UserAuth
import com.bookshelfhub.bookshelfhub.services.database.cloud.CloudDb
import com.bookshelfhub.bookshelfhub.services.database.local.LocalDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.User
import com.google.common.base.Optional
import kotlinx.coroutines.runBlocking

class UploadUserData(val context: Context, workerParams: WorkerParameters): CoroutineWorker(context,
    workerParams
) {

    override suspend fun doWork(): Result {
        val userAuth=UserAuth(StringUtil())
        val userId = userAuth.getUserId()
        val localDb = LocalDb(context)
        val user = localDb.getUser(userId)

        val userData = user.get()
            if (user.isPresent && !userData.uploaded){
               CloudDb().addDataAsync(userData, DbFields.USERS_COLL.KEY, userId, DbFields.USER.KEY){
                   userData.uploaded = true
                        runBlocking {
                            localDb.addUser(userData)
                        }
                    }
            }
        return Result.success()
    }
}