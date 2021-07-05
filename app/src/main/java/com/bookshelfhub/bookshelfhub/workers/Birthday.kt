package com.bookshelfhub.bookshelfhub.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class Birthday(val context: Context, workerParams: WorkerParameters): Worker(context,
    workerParams
) {
    override fun doWork(): Result {




        return Result.success()
    }
}