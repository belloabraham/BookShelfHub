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

@HiltWorker
class UpdatePublishedBooks @AssistedInject constructor (
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val userAuth:IUserAuth,
    private val cloudDb:ICloudDb,
    private val util: Util,
    private val localDb: ILocalDb
) : CoroutineWorker(context,
    workerParams
) {
    override suspend fun doWork(): Result {

        if (!userAuth.getIsUserAuthenticated()){
            return Result.retry()
        }

      val task =  cloudDb.getListOfDataWhereAsync(
            DbFields.PUBLISHED_BOOKS.KEY,
            DbFields.PUBLISHED.KEY, true,
        )

        return if(task.isSuccessful){

            val publishedBooks = util.queryToListType(task.result, PublishedBook::class.java)

            if(publishedBooks.isNotEmpty()){
                localDb.addAllPubBooks(publishedBooks)
            }

            Result.success()
        }else{
            Result.retry()
        }

    }


}