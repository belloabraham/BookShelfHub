package com.bookshelfhub.bookshelfhub.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bookshelfhub.bookshelfhub.helpers.utils.Logger
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.RemoteDataFields
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.IRemoteDataSource
import com.bookshelfhub.bookshelfhub.helpers.database.ILocalDb
import com.bookshelfhub.bookshelfhub.data.models.entities.PublishedBook
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.Util
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.tasks.await

@HiltWorker
class UnPublishedBooks @AssistedInject constructor (
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val userAuth:IUserAuth,
    private val util: Util,
    private val localDb: ILocalDb,
    private val remoteDataSource: IRemoteDataSource
) : CoroutineWorker(context,
    workerParams
) {

    override suspend fun doWork(): Result {

        if (!userAuth.getIsUserAuthenticated()){
            return Result.retry()
        }


       return  try {
            //Get all Published books where published == false
            val querySnapshot =  remoteDataSource.getListOfDataWhereAsync(
                RemoteDataFields.PUBLISHED_BOOKS.KEY,
                RemoteDataFields.PUBLISHED.KEY,false,
            ).await()

            val unPublishedBooks =  util.queryToListType(querySnapshot, PublishedBook::class.java)

            if(unPublishedBooks.isEmpty()){
                localDb.deleteUnPublishedBookRecords(unPublishedBooks)
            }

            Result.success()

        }catch (e:Exception){
           Logger.log("Worker:UnPublishedBooks", e)
           Result.retry()
        }

    }
}