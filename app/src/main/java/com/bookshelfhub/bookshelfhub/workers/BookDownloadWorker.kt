package com.bookshelfhub.bookshelfhub.workers

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.bookshelfhub.bookshelfhub.data.Book
import com.bookshelfhub.bookshelfhub.data.FileExtension
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.data.repos.bookmarks.IBookmarksRepo
import com.bookshelfhub.bookshelfhub.helpers.cloudstorage.FirebaseCloudStorage
import com.bookshelfhub.bookshelfhub.helpers.cloudstorage.ICloudStorage
import com.bookshelfhub.bookshelfhub.helpers.notification.NotificationBuilder
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import timber.log.Timber

@HiltWorker
class BookDownloadWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val cloudStorage: ICloudStorage,
): CoroutineWorker(context, workerParams){

    private var message =""
    private var bookName =""
    private var notificationId = 0

    override suspend fun doWork(): Result {

         val pubId = inputData.getString(Book.PUB_ID)!!
         val bookId = inputData.getString(Book.ID)!!
         bookName = inputData.getString(Book.NAME)!!
         notificationId = inputData.getInt(Book.SERIAL_NO, 0)

       cloudStorage.downloadFile(
            pubId,
            bookId,
            bookId,
            FileExtension.DOT_PDF,
            onProgress = { progress->
                message = getMessage(progress)
                runBlocking {
                    setForeground(getForegroundInfo())
                    //Add download progress to the database
                }
            },
            onComplete = {
                     runBlocking {
                         //Add to database that download complete
                     }
            },
            onError =  {
                runBlocking {
                    //Show error to user through database
                }
            })

        return Result.success()
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        val notification = getNotificationBuilder(bookName, message).getNotificationBuiler().build()
        return ForegroundInfo(notificationId, notification)
    }

    private fun getMessage(progress:Long): String {
      return  if( progress == 100L ) "Download complete" else "Downloading $progress%"
    }

    private fun getNotificationBuilder(
        title: String,
        message: String,
    ): NotificationBuilder.Builder {
        return NotificationBuilder(applicationContext)
            .setAutoCancel(false)
            .setOngoing(true)
            .setMessage(message)
            .setTitle(title)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .Builder(applicationContext)
    }

}