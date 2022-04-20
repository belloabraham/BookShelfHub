package com.bookshelfhub.bookshelfhub.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bookshelfhub.bookshelfhub.data.repos.BookInterestRepo
import com.bookshelfhub.bookshelfhub.data.repos.ReadHistoryRepo
import com.bookshelfhub.bookshelfhub.helpers.utils.Logger
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.RemoteDataFields
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.tasks.await

@HiltWorker
class UploadBookInterest @AssistedInject constructor (
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val bookInterestRepo: BookInterestRepo,
    private val userAuth:IUserAuth):
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
              Logger.log("Worker:UploadBkInterest", e)
              Result.retry()
          }

    }
}