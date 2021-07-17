package com.bookshelfhub.bookshelfhub.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.bookshelfhub.bookshelfhub.Utils.StringUtil
import com.bookshelfhub.bookshelfhub.enums.DbFields
import com.bookshelfhub.bookshelfhub.services.authentication.UserAuth
import com.bookshelfhub.bookshelfhub.services.database.cloud.CloudDb
import com.bookshelfhub.bookshelfhub.services.database.local.LocalDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.PublishedBooks
import com.bookshelfhub.bookshelfhub.wrapper.Json
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class UnPublishedBooks(val context: Context, workerParams: WorkerParameters) : CoroutineWorker(context,
    workerParams
) {

    override suspend fun doWork(): Result {

        val userAuth = UserAuth()

        if (!userAuth.getIsUserAuthenticated()){
            return Result.retry()
        }

        CloudDb(Json(Gson())).getListOfDataAsync(
            DbFields.PUBLISHED_BOOKS.KEY,
            DbFields.PUBLISHED_BOOK.KEY,
            PublishedBooks::class.java,
        ) {
            //if(it.isNotEmpty()){
                var listOfUnPublishedBooksIsbn= emptyList<String>()
                for (publishBooks in it){
                    listOfUnPublishedBooksIsbn  = listOfUnPublishedBooksIsbn.plus(publishBooks.isbn)
                }
                val localDb = LocalDb(context)

                coroutineScope {
                    localDb.deletePublishedBookRecords(listOfUnPublishedBooksIsbn)
                }

           // }
         }
        return Result.success()
    }
}