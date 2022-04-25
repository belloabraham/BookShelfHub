package com.bookshelfhub.bookshelfhub.workers

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.data.Book
import com.bookshelfhub.bookshelfhub.data.FileExtension
import com.bookshelfhub.bookshelfhub.data.models.uistate.BookDownloadState
import com.bookshelfhub.bookshelfhub.data.repos.bookdownload.BookDownloadStateRepo
import com.bookshelfhub.bookshelfhub.helpers.AppExternalStorage
import com.bookshelfhub.bookshelfhub.helpers.cloudstorage.ICloudStorage
import com.bookshelfhub.bookshelfhub.helpers.notification.NotificationBuilder
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import java.io.File
import java.io.IOException

@HiltWorker
class DownloadBook @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val cloudStorage: ICloudStorage,
    private val bookDownloadStateRepo: BookDownloadStateRepo
): CoroutineWorker(context, workerParams){

    private var message =""
    private var bookName =""
    private var notificationId = 0

    override suspend fun doWork(): Result {

         val pubId = inputData.getString(Book.PUB_ID)!!
         val bookId = inputData.getString(Book.ID)!!
         bookName = inputData.getString(Book.NAME)!!
         notificationId = inputData.getInt(Book.SERIAL_NO, 0)

        try {

            val localFile = AppExternalStorage.getDocumentFilePath(
                folderName = pubId,
                subFolderName = bookId,
                fileNameWithExt = "$bookId+${FileExtension.DOT_PDF}",
                applicationContext)

            if(localFile.exists()){
                return Result.success()
            }

            cloudStorage.downloadAsTempFile(
                folder =  pubId,
                subfolder =  bookId,
                fileName =  bookId,
                remoteFileExt = FileExtension.DOT_PDF,
                onProgress = { it->
                    val progress = it-10
                    message = getMessage(progress)
                    runBlocking {
                        setForeground(getForegroundInfo())
                        bookDownloadStateRepo.updatedDownloadState(bookId, progress)
                    }
                },
                onComplete = {
                    runBlocking {
                        val tempFile = AppExternalStorage.getDocumentFilePath(
                            folderName =  pubId,
                            subFolderName = bookId,
                            fileNameWithExt = bookId+FileExtension.DOT_TEMP,
                            applicationContext)

                        createLocalFileFromTempFile(tempFile, localFile, bookId)
                        bookDownloadStateRepo.updatedDownloadState(bookId, 100)
                        message = getMessage(100)
                        setForeground(getForegroundInfo())
                    }
                },
                onError =  {
                    runBlocking {
                        bookDownloadStateRepo.updatedDownloadState(bookId, true)
                    }
                })
        }catch (e:Exception){
            Timber.e(e)
            return Result.failure()
        }

        return Result.success()
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        val notification = getNotificationBuilder(bookName, message).getNotificationBuilder().build()
        return ForegroundInfo(notificationId, notification)
    }

    private fun getMessage(progress:Int): String {
      return  if( progress == 100 )  "Download complete" else "Downloading $progress%"
    }

    private fun getNotificationBuilder(
        title: String,
        message: String,
    ): NotificationBuilder.Builder {
      //  val pendingIntent = WorkManager.getInstance(applicationContext)
         //   .createCancelPendingIntent(id)
        return NotificationBuilder(applicationContext)
            .setAutoCancel(false)
            .setOngoing(true)
            .setMessage(message)
            .setTitle(title)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .Builder(applicationContext)
    }

    @Throws(IOException::class)
    private fun createLocalFileFromTempFile(fromFile: File, renameToFile: File, bookId:String){
        try {

            if(renameToFile.exists()){
                val unableToDelete =  !renameToFile.delete()
                if (unableToDelete){
                    throw IOException("Existing book $bookId deletion Failed")
                }
            }

            val unableToRename = !fromFile.renameTo(renameToFile)
            if(unableToRename){
                throw IOException("Unable to rename to $bookId")
            }

        }finally {
            if(fromFile.exists()){
                fromFile.delete()
            }
        }
    }

}