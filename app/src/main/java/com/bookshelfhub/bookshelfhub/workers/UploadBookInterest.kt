package com.bookshelfhub.bookshelfhub.workers

import android.content.Context
import android.util.Log
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
class UploadBookInterest @AssistedInject constructor (
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val localDb: ILocalDb, private val cloudDb: ICloudDb, private val userAuth:IUserAuth):
    CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {

        if (!userAuth.getIsUserAuthenticated()){
            return Result.retry()
        }
        val userId = userAuth.getUserId()

         val bookInterest = localDb.getBookInterest(userId)

      return  if (bookInterest.isPresent && !bookInterest.get().uploaded){

            val bookInterestData = bookInterest.get()

          try {
              cloudDb.addDataAsync(bookInterestData, DbFields.USERS.KEY, userId, DbFields.BOOK_INTEREST.KEY).await()

              bookInterestData.uploaded=true
              localDb.addBookInterest(bookInterestData)
              Result.success()

          }catch (e:Exception){
              Logger.log("Worker:UploadBkInterest", e)
              Result.retry()
          }

        }else{
          Result.success()
      }

    }
}