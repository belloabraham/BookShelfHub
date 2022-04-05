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
import com.bookshelfhub.bookshelfhub.data.models.entities.PublishedBook
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.Util
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.tasks.await

@HiltWorker
class UpdatePublishedBooks @AssistedInject constructor (
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val userAuth:IUserAuth,
    private val cloudDb: ICloudDb,
    private val util: Util,
    private val localDb: ILocalDb
) : CoroutineWorker(context,
    workerParams
) {
    override suspend fun doWork(): Result {

        if (!userAuth.getIsUserAuthenticated()){
            return Result.retry()
        }

       return try {

           val querySnapshot =  cloudDb.getListOfDataWhereAsync(
               DbFields.PUBLISHED_BOOKS.KEY,
               DbFields.PUBLISHED.KEY, true,
           ).await()

           val publishedBooks = util.queryToListType(querySnapshot, PublishedBook::class.java)

           if(publishedBooks.isNotEmpty()){
               localDb.addAllPubBooks(publishedBooks)
           }
           Result.success()
        }catch (e:Exception){
           Logger.log("Worker:UpdatePubBooks", e)
           Result.retry()
        }

    }


}