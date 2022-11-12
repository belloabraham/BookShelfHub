package com.bookshelfhub.core.data.repos.bookinterest

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bookshelfhub.core.authentication.IUserAuth
import com.bookshelfhub.core.common.helpers.ErrorUtil
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class UploadBookInterest @AssistedInject constructor (
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val bookInterestRepo: IBookInterestRepo,
    private val userAuth: IUserAuth
):
    CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {

        if (!userAuth.getIsUserAuthenticated()){
            return Result.retry()
        }

        val userId = userAuth.getUserId()

         val bookInterest = bookInterestRepo.getBookInterest(userId)
        val bookInterestAvailableForUpload = bookInterest.isPresent && !bookInterest.get().uploaded

        if (!bookInterestAvailableForUpload){
            return Result.success()
        }


         return  try {
              val bookInterestData = bookInterest.get()
              bookInterestRepo.updateRemoteUserBookInterest(bookInterestData, userId)
              bookInterestData.uploaded=true
              bookInterestRepo.addBookInterest(bookInterestData)
              Result.success()

          }catch (e:Exception){
             ErrorUtil.e(e)
              Result.retry()
          }

    }
}