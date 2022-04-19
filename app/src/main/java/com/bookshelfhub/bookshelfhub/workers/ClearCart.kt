package com.bookshelfhub.bookshelfhub.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bookshelfhub.bookshelfhub.data.repos.CartItemsRepo
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class ClearCart @AssistedInject constructor (
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val cartItemsRepo: CartItemsRepo,
) : CoroutineWorker(context,
    workerParams
) {

    override suspend fun doWork(): Result {
        cartItemsRepo.deleteAllCartItems()
        return Result.success()
    }
}