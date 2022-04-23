package com.bookshelfhub.bookshelfhub.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.data.repos.bookmarks.IBookmarksRepo
import com.bookshelfhub.bookshelfhub.helpers.cloudstorage.FirebaseCloudStorage
import com.bookshelfhub.bookshelfhub.helpers.cloudstorage.ICloudStorage
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber

@HiltWorker
class BookDownloadWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val cloudStorage: ICloudStorage,
): CoroutineWorker(context,
workerParams
){
    override suspend fun doWork(): Result {



        return Result.success()
    }

}