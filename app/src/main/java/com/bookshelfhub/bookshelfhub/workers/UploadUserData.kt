package com.bookshelfhub.bookshelfhub.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bookshelfhub.bookshelfhub.Utils.Logger
import com.bookshelfhub.bookshelfhub.services.database.cloud.DbFields
import com.bookshelfhub.bookshelfhub.services.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.services.database.cloud.ICloudDb
import com.bookshelfhub.bookshelfhub.helpers.database.ILocalDb
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await

@HiltWorker
class UploadUserData  @AssistedInject constructor (
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val localDb: ILocalDb,
    private val userAuth:IUserAuth,
    private val cloudDb: ICloudDb): CoroutineWorker(context,
    workerParams
) {

    override suspend fun doWork(): Result {

        val userId = userAuth.getUserId()
        val user = localDb.getUser(userId)

        val userData = user.get()
         return   if (user.isPresent && !userData.uploaded){

             try {
                 val task =  cloudDb.addDataAsync(userData, DbFields.USERS.KEY, userId, DbFields.USER.KEY).await()

                 task.run {
                     userData.uploaded = true
                     localDb.addUser(userData)
                     Result.success()
                 }

             }catch (e:Exception){
                 Logger.log("Worker:UploadUserData", e)
                 Result.retry()
             }

            }else{
             Result.success()
            }

    }
}