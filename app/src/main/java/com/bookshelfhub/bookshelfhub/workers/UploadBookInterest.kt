package com.bookshelfhub.bookshelfhub.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.bookshelfhub.bookshelfhub.Utils.StringUtil
import com.bookshelfhub.bookshelfhub.enums.DbFields
import com.bookshelfhub.bookshelfhub.services.authentication.UserAuth
import com.bookshelfhub.bookshelfhub.services.database.cloud.CloudDb
import com.bookshelfhub.bookshelfhub.services.database.local.LocalDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.BookInterest
import com.google.common.base.Optional
import kotlinx.coroutines.runBlocking

class UploadBookInterest (val context: Context, workerParams: WorkerParameters): Worker(context,
    workerParams
) {

    override fun doWork(): Result {
        val userAuth=UserAuth(StringUtil())

        if (!userAuth.getIsUserAuthenticated()){
            Result.retry()
        }

        val userId = userAuth.getUserId()

        val localDb = LocalDb(context)
         val  bookInterest: Optional<BookInterest>
             runBlocking {
                 bookInterest = localDb.getBookInterest(userId)
             }

        if (bookInterest.isPresent && !bookInterest.get().uploaded){
            val bookInterestData = bookInterest.get()
               CloudDb().addDataAsync(bookInterestData, DbFields.USERS_COLL.KEY, userId, DbFields.BOOK_INTEREST.KEY){
                   bookInterestData.uploaded=true
                runBlocking {
                    localDb.addBookInterest(bookInterestData)
                }
            }
        }

        return Result.success()
    }
}