package com.bookshelfhub.bookshelfhub.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.data.repos.publishedbooks.IPublishedBooksRepo
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber

@HiltWorker
class UnPublishedBooks @AssistedInject constructor (
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val userAuth:IUserAuth,
    private val publishedBooksRepo: IPublishedBooksRepo,
) : CoroutineWorker(context,
    workerParams
) {

    override suspend fun doWork(): Result {

        val userNotAuthenticated = !userAuth.getIsUserAuthenticated()
        if (userNotAuthenticated){
            return Result.retry()
        }

       return  try {
            val unPublishedBooks =  publishedBooksRepo.getListOfRemoteUnpublishedBooks()
            if(unPublishedBooks.isEmpty()){
                publishedBooksRepo.deleteUnPublishedBookRecords(unPublishedBooks)
            }
            Result.success()
        }catch (e:Exception){
           Timber.e(e)
           Result.retry()
        }

    }
}