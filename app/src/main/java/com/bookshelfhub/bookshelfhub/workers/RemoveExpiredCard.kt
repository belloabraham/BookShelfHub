package com.bookshelfhub.bookshelfhub.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bookshelfhub.bookshelfhub.Utils.datetime.DateTimeUtil
import com.bookshelfhub.bookshelfhub.services.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.services.database.cloud.DbFields
import com.bookshelfhub.bookshelfhub.services.database.cloud.ICloudDb
import com.bookshelfhub.bookshelfhub.services.database.local.ILocalDb
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.coroutineScope

@HiltWorker
class RemoveExpiredCard @AssistedInject constructor (
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val localDb:ILocalDb,
) : CoroutineWorker(context,
    workerParams
) {

    override suspend fun doWork(): Result {
        val cards = localDb.getPaymentCards()

        if (cards.isNotEmpty()) {

        for (card in cards) {
            if (card.expiryMonth >= DateTimeUtil.getMonth()){
                localDb.deletePaymentCard(card)
            }
        }
    }
        return Result.success()
    }
}