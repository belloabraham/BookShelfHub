package com.bookshelfhub.bookshelfhub.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bookshelfhub.bookshelfhub.helpers.utils.Logger
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.DbFields
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.ICloudDb
import com.bookshelfhub.bookshelfhub.helpers.database.ILocalDb
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.tasks.await

@HiltWorker
class UploadUserData  @AssistedInject constructor (
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val localDb: ILocalDb,
    private val userAuth:IUserAuth,
    private val cloudDb: ICloudDb
): CoroutineWorker(context,
    workerParams
) {

    override suspend fun doWork(): Result {

        val userId = userAuth.getUserId()
        val user = localDb.getUser(userId)

        val userData = user.get()
         return   if (user.isPresent && !userData.uploaded){

             try {
                 cloudDb.addDataAsync(userData, DbFields.USERS.KEY, userId, DbFields.USER.KEY).await()

                     userData.uploaded = true
                     localDb.addUser(userData)
                     Result.success()

             }catch (e:Exception){
                 Logger.log("Worker:UploadUserData", e)
                 Result.retry()
             }

            }else{
                Result.success()
            }

    }
}