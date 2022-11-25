package com.bookshelfhub.book.page.workers

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.bookshelfhub.book.page.BookActivity
import com.bookshelfhub.core.common.helpers.ErrorUtil
import com.bookshelfhub.core.common.helpers.storage.AppExternalStorage
import com.bookshelfhub.core.common.helpers.storage.FileExtension
import com.bookshelfhub.core.common.notification.NotificationBuilder
import com.bookshelfhub.core.data.Book
import com.bookshelfhub.core.data.repos.bookdownload.IBookDownloadStateRepo
import com.bookshelfhub.core.remote.storage.ICloudStorage
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.runBlocking
import com.bookshelfhub.core.resources.R
import java.io.File
import java.io.IOException


@HiltWorker
class DownloadBook @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val cloudStorage: ICloudStorage,
    private val bookDownloadStateRepo: IBookDownloadStateRepo
): CoroutineWorker(context, workerParams){

    private lateinit var message:String
    private lateinit var bookName:String
    private val progressMax = 100
    private var progress = 0
    private var notificationId = 1
    private lateinit var notification:NotificationCompat.Builder

    override suspend fun doWork(): Result {

        val pubId = inputData.getString(Book.PUB_ID)!!
        val bookId = inputData.getString(Book.ID)!!
        bookName = inputData.getString(Book.NAME)!!
        notificationId = inputData.getInt(Book.SERIAL_NO, 1)
        message = applicationContext.getString(R.string.downloading_in_prog)
        notification = getNotificationBuilder(bookName, message).getNotificationBuilder()
        notification.setProgress(progressMax, 0 , true)
        NotificationManagerCompat.from(context).notify(notificationId, notification.build())

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
                onProgress = {
                    progress = it
                    message = getDownloadProgressMessage(progress)
                    runBlocking {
                        setForeground(getForegroundInfo())
                        bookDownloadStateRepo.updatedDownloadState(bookId, progress)
                    }
                },
                onComplete = {
                    runBlocking {
                        progress = 100
                        val tempLocalFilePath = AppExternalStorage.getDocumentFilePath(
                            folderName =  pubId,
                            subFolderName = bookId,
                            fileNameWithExt = bookId+FileExtension.DOT_TEMP,
                            applicationContext
                        )

                        notification.setOngoing(false)
                        notification.setAutoCancel(true)

                        val intent = Intent(applicationContext, BookActivity::class.java)
                        with(intent) {
                            putExtra(Book.NAME, bookName)
                            putExtra(Book.ID, bookId)
                        }
                        val  bookActivityPendingIntent = PendingIntent.getActivity(
                            applicationContext,
                            0, intent,
                            NotificationBuilder.getIntentFlag()
                        )
                        notification.setContentIntent(bookActivityPendingIntent)
                        createLocalFileFromTempFile(tempLocalFilePath, localFile, bookId)
                        bookDownloadStateRepo.updatedDownloadState(bookId, progress)
                        message = getDownloadProgressMessage(progress)
                        setForeground(getForegroundInfo())
                    }
                },
                onError =  {
                    runBlocking {
                        bookDownloadStateRepo.updatedDownloadState(bookId, true)
                    }
                })

        }catch (e:Exception){
            ErrorUtil.e(e)
            return Result.failure()
        }

        return Result.success()
    }


    override suspend fun getForegroundInfo(): ForegroundInfo {
        notification.setProgress(progressMax, progress, false)
        notification.setContentText(message)
        return ForegroundInfo(notificationId, notification.build())
    }

    private fun getDownloadProgressMessage(progress:Int): String {
        return if(progress == 100 ){
           applicationContext.getString(R.string.download_complete)
        }else{
            String.format(
                applicationContext.getString(R.string.download_progress),
                progress
            )
        }
    }

    private fun getNotificationBuilder(
        title: String,
        message: String,
    ): NotificationBuilder.Builder {
        return NotificationBuilder(
            applicationContext,
            applicationContext.getString(R.string.download_channel_id)
        )
            .setAutoCancel(false)
            .setOngoing(true)
            .setContentText(message)
            .setTitle(title)
            .setAlertOnlyOnce(true)
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