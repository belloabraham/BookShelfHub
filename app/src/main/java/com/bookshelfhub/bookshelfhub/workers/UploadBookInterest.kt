package com.bookshelfhub.bookshelfhub.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bookshelfhub.bookshelfhub.Utils.SettingsUtil
import com.bookshelfhub.bookshelfhub.enums.DbFields
import com.bookshelfhub.bookshelfhub.services.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.services.authentication.firebase.FBUserAuth
import com.bookshelfhub.bookshelfhub.services.database.cloud.Firestore
import com.bookshelfhub.bookshelfhub.services.database.cloud.ICloudDb
import com.bookshelfhub.bookshelfhub.services.database.local.ILocalDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.RoomDb
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

@HiltWorker
class UploadBookInterest @AssistedInject constructor (
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val localDb: ILocalDb, private val cloudDb: ICloudDb, private val userAuth:IUserAuth):
    CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {

        if (!userAuth.getIsUserAuthenticated()){
            return Result.retry()
        }
        val userId = userAuth.getUserId()

         val bookInterest = localDb.getBookInterest(userId)

        if (bookInterest.isPresent && !bookInterest.get().uploaded){
            val bookInterestData = bookInterest.get()
               cloudDb.addDataAsync(bookInterestData, DbFields.USERS_COLL.KEY, userId, DbFields.BOOK_INTEREST.KEY){
                   bookInterestData.uploaded=true
                coroutineScope {
                    localDb.addBookInterest(bookInterestData)
                }
            }
        }

        return Result.success()
    }
}