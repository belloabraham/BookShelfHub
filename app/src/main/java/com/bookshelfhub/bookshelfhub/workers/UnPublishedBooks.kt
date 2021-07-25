package com.bookshelfhub.bookshelfhub.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bookshelfhub.bookshelfhub.enums.DbFields
import com.bookshelfhub.bookshelfhub.services.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.services.authentication.firebase.FBUserAuth
import com.bookshelfhub.bookshelfhub.services.database.cloud.Firestore
import com.bookshelfhub.bookshelfhub.services.database.cloud.ICloudDb
import com.bookshelfhub.bookshelfhub.services.database.local.ILocalDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.RoomDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.PublishedBooks
import com.bookshelfhub.bookshelfhub.wrapper.Json
import com.google.gson.Gson
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.coroutineScope

@HiltWorker
class UnPublishedBooks @AssistedInject constructor (
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
    val userAuth:IUserAuth,
    val cloudDb:ICloudDb) : CoroutineWorker(context,
    workerParams
) {

    override suspend fun doWork(): Result {


        if (!userAuth.getIsUserAuthenticated()){
            return Result.retry()
        }

        cloudDb.getListOfDataAsync(
            DbFields.UNPUBLISHED_BOOKS.KEY,
            DbFields.UNPUBLISHED_BOOK.KEY,
            PublishedBooks::class.java,
        ) {
            if(it.isNotEmpty()){
                var listOfUnPublishedBooksIsbn= emptyList<String>()
                for (publishBooks in it){
                    listOfUnPublishedBooksIsbn  = listOfUnPublishedBooksIsbn.plus(publishBooks.isbn)
                }
                val localDb:ILocalDb = RoomDb(context)

                coroutineScope {
                    localDb.deleteUnPublishedBookRecords(listOfUnPublishedBooksIsbn)
                }

            }
         }
        return Result.success()
    }
}