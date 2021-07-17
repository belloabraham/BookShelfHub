package com.bookshelfhub.bookshelfhub.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bookshelfhub.bookshelfhub.services.database.local.LocalDb

class TrendingBooks (val context: Context, workerParams: WorkerParameters) : CoroutineWorker(context,
    workerParams
) {
    override suspend fun doWork(): Result {
        val localDb = LocalDb(context)
        localDb.updateTrendingBooksRecords()
        return Result.success()
    }


}