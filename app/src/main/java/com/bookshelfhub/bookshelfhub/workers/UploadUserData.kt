package com.bookshelfhub.bookshelfhub.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bookshelfhub.bookshelfhub.services.database.cloud.DbFields
import com.bookshelfhub.bookshelfhub.services.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.services.database.cloud.ICloudDb
import com.bookshelfhub.bookshelfhub.services.database.local.ILocalDb
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.coroutineScope

@HiltWorker
class UploadUserData  @AssistedInject constructor (
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val localDb: ILocalDb, private val userAuth:IUserAuth, private val cloudDb: ICloudDb): CoroutineWorker(context,
    workerParams
) {

    override suspend fun doWork(): Result {

        val userId = userAuth.getUserId()
        val user = localDb.getUser(userId)

        val userData = user.get()
            if (user.isPresent && !userData.uploaded){
            val task =  cloudDb.addDataAsync(userData, DbFields.USERS.KEY, userId, DbFields.USER.KEY){
                   userData.uploaded = true
                        coroutineScope {
                            localDb.addUser(userData)
                        }
                    }

                if (!task.isSuccessful){
                    return Result.retry()
                }

            }
        return Result.success()
    }
}