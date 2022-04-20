package com.bookshelfhub.bookshelfhub.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bookshelfhub.bookshelfhub.helpers.utils.Logger
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.RemoteDataFields
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.IRemoteDataSource
import com.bookshelfhub.bookshelfhub.data.models.entities.PublishedBook
import com.bookshelfhub.bookshelfhub.data.repos.PublishedBooksRepo
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.tasks.await

@HiltWorker
class UnPublishedBooks @AssistedInject constructor (
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val userAuth:IUserAuth,
    private val publishedBooksRepo: PublishedBooksRepo,
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
           Logger.log("Worker:UnPublishedBooks", e)
           Result.retry()
        }

    }
}