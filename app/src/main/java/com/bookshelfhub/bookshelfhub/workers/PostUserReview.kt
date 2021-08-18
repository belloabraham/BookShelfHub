package com.bookshelfhub.bookshelfhub.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bookshelfhub.bookshelfhub.const.Regex
import com.bookshelfhub.bookshelfhub.enums.Book
import com.bookshelfhub.bookshelfhub.enums.DbFields
import com.bookshelfhub.bookshelfhub.extensions.containsUrl
import com.bookshelfhub.bookshelfhub.services.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.services.database.cloud.ICloudDb
import com.bookshelfhub.bookshelfhub.services.database.local.ILocalDb
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.coroutineScope


@HiltWorker
class PostUserReview @AssistedInject constructor(
    @Assisted val context: Context, @Assisted workerParams: WorkerParameters,
    private val localDb: ILocalDb, private val cloudDb: ICloudDb, private val userAuth: IUserAuth
): CoroutineWorker(context,
    workerParams
){

    override suspend fun doWork(): Result {
        val isbn = inputData.getString(Book.ISBN.KEY)!!
        val userReview =  localDb.getUserReview(isbn).get()

        if(userReview.verified && !userReview.review.containsUrl(Regex.URL_IN_TEXT)){
            val userId = userAuth.getUserId()
            cloudDb.addDataAsync(userReview, DbFields.PUBLISHED_BOOKS.KEY, isbn, DbFields.REVIEWS.KEY, userId){

            }

        }

        return Result.success()
    }
}
