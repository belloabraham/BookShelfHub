package com.bookshelfhub.bookshelfhub.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.bookshelfhub.bookshelfhub.Utils.StringUtil
import com.bookshelfhub.bookshelfhub.enums.DbFields
import com.bookshelfhub.bookshelfhub.services.authentication.UserAuth
import com.bookshelfhub.bookshelfhub.services.database.cloud.CloudDb
import com.bookshelfhub.bookshelfhub.services.database.local.LocalDb
import kotlinx.coroutines.runBlocking

class UploadBookInterest (val context: Context, workerParams: WorkerParameters): Worker(context,
    workerParams
) {

    override fun doWork(): Result {
        val userAuth=UserAuth(StringUtil())
        val userId = userAuth.getUserId()
        val localDb = LocalDb(context)
        val  bookInterest = localDb.getBookInterest(userId)
        val bookInterestData = bookInterest.get()
        if (bookInterest.isPresent && !bookInterestData.uploaded){
               CloudDb().addDataAsync(bookInterestData, DbFields.USERS_COLL.KEY, userId, DbFields.BOOK_INTEREST.KEY){
               val newBookInterestData = bookInterestData.copy(uploaded = true)
                runBlocking {
                    localDb.addBookInterest(newBookInterestData)
                }
            }
        }

        return Result.success()
    }
}