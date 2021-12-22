package com.bookshelfhub.bookshelfhub.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bookshelfhub.bookshelfhub.services.database.cloud.DbFields
import com.bookshelfhub.bookshelfhub.services.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.services.database.cloud.ICloudDb
import com.bookshelfhub.bookshelfhub.helpers.database.ILocalDb
import com.bookshelfhub.bookshelfhub.helpers.database.room.entities.PublishedBook
import com.bookshelfhub.bookshelfhub.services.database.Util
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await

@HiltWorker
class UnPublishedBooks @AssistedInject constructor (
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val userAuth:IUserAuth,
    private val util: Util,
    private val localDb: ILocalDb,
    private val cloudDb:ICloudDb) : CoroutineWorker(context,
    workerParams
) {

    override suspend fun doWork(): Result {

        if (!userAuth.getIsUserAuthenticated()){
            return Result.retry()
        }


       return  try {
            //Get all Published books where published == false
            val querySnapshot =  cloudDb.getListOfDataWhereAsync(
                DbFields.PUBLISHED_BOOKS.KEY,
                DbFields.PUBLISHED.KEY,false,
            ).await()

            val unPublishedBooks =  util.queryToListType(querySnapshot, PublishedBook::class.java)

            if(unPublishedBooks.isEmpty()){
                localDb.deleteUnPublishedBookRecords(unPublishedBooks)
            }

            Result.success()

        }catch (e:Exception){
            Result.retry()
        }

    }
}