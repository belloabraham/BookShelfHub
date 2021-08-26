package com.bookshelfhub.bookshelfhub.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bookshelfhub.bookshelfhub.services.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.services.database.cloud.DbFields
import com.bookshelfhub.bookshelfhub.services.database.cloud.ICloudDb
import com.bookshelfhub.bookshelfhub.services.database.local.ILocalDb
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay

@HiltWorker
class UploadTransactions @AssistedInject constructor (
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val userAuth:IUserAuth, private val localDb:ILocalDb,
    private val cloudDb: ICloudDb
) : CoroutineWorker(context,
    workerParams
) {

    override suspend fun doWork(): Result {

        if (userAuth.getIsUserAuthenticated()){

            val paymentTrans  = localDb.getAllPaymentTransactions()
            val userId = userAuth.getUserId()

           if (paymentTrans.isNotEmpty()) {
                cloudDb.addListOfDataAsync(DbFields.USERS.KEY, userId, DbFields.TRANSACTIONS.KEY, paymentTrans){
                    coroutineScope {
                        localDb.deleteAllPaymentTransactions()
                    }
                }
           }
        }

        return Result.success()
    }
}