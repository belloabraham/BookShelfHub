package com.bookshelfhub.book.page.workers

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.ForegroundInfo
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.bookshelfhub.book.page.BookActivity
import com.bookshelfhub.core.common.helpers.ErrorUtil
import com.bookshelfhub.core.common.helpers.storage.FileExtension
import com.bookshelfhub.core.common.notification.NotificationBuilder
import com.bookshelfhub.core.data.Book
import com.bookshelfhub.core.data.repos.bookdownload.IBookDownloadStateRepo
import com.bookshelfhub.core.domain.usecases.GetBookIdFromCompoundId
import com.bookshelfhub.core.domain.usecases.LocalFile
import com.bookshelfhub.core.remote.storage.ICloudStorage
import com.bookshelfhub.core.remote.storage.StoragePath
import com.bookshelfhub.core.resources.R
import com.google.android.gms.tasks.Tasks
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.IOException

@HiltWorker
class DownloadBook @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val cloudStorage: ICloudStorage,
    private val getBookIdFromCompoundId: GetBookIdFromCompoundId,
    private val bookDownloadStateRepo: IBookDownloadStateRepo
): Worker(context, workerParams){

    private var message:String = ""
    private var bookName:String = ""
    private val progressMax = 100
    private var progress = 0
    private var notificationId = 1
    private lateinit var notification:NotificationCompat.Builder
    private val SEPERATOR = "/"

    override fun doWork(): Result {

        val pubId = inputData.getString(Book.PUB_ID)!!
        val bookId = inputData.getString(Book.ID)!!
        bookName = inputData.getString(Book.NAME)!!
        notificationId = inputData.getInt(Book.SERIAL_NO, 1)
        message = applicationContext.getString(R.string.downloading_in_prog)

        val unMergedBookId = getBookIdFromCompoundId(bookId)

        try {

            notification = getNotificationBuilder(bookName, message).getNotificationBuilder()
            setForegroundAsync(getDownloadForegroundInfo(notification))

            val localFile = LocalFile.getBookFile(bookId, pubId, applicationContext)

            if(localFile.exists()){
                return Result.success()
            }

            val tempLocalFilePath = LocalFile.getBookFile(bookId, pubId, applicationContext, FileExtension.DOT_TEMP)

            val remoteFilePath = "${StoragePath.PUBLISHED_BOOKS}$SEPERATOR$pubId$SEPERATOR$bookId$SEPERATOR$unMergedBookId${FileExtension.DOT_PDF}"

            val storageTask = cloudStorage.downloadAsTempFile(
                remoteFilePath,
                tempLocalFilePath,
                 onProgress = {
                    notification = getNotificationBuilder(bookName, message).getNotificationBuilder()
                    progress = it
                    message = getDownloadProgressMessage(progress)
                     setForegroundAsync(getDownloadForegroundInfo(notification))

                     runBlocking {
                        bookDownloadStateRepo.updatedDownloadState(bookId, progress)
                    }
                },
                onComplete = { downloadTask->

                    if(!downloadTask.isSuccessful){
                        notification = getNotificationBuilder(bookName, "Download Error").getNotificationBuilder()
                    }

                    if(downloadTask.isSuccessful){
                        progress = 100
                        message = getDownloadProgressMessage(progress)
                        notification = getNotificationBuilder(bookName, message).getNotificationBuilder()

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

                        runBlocking {
                            bookDownloadStateRepo.updatedDownloadState(bookId, progress)
                        }
                    }

                    notification.setOngoing(false)
                    notification.setAutoCancel(true)

                   setForegroundAsync(getDownloadForegroundInfo(notification))
                },
                onError =  {
                    ErrorUtil.e(it)
                    runBlocking {
                        bookDownloadStateRepo.updatedDownloadState(bookId, true)
                    }
                })

            Tasks.await(storageTask)

        }catch (e:Exception){
            ErrorUtil.e(e)
            return Result.failure()
        }

        return Result.success()
    }

     private fun getDownloadForegroundInfo(notification:NotificationCompat.Builder): ForegroundInfo {
        val isDeterminate = this.progress == 0
        notification.setProgress(progressMax, progress, isDeterminate)
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
                    throw IOException("$bookId deletion Failed")
                }
            }

            val unableToRename = !fromFile.renameTo(renameToFile)
            if(unableToRename){
                throw IOException("Unable to rename to $bookId")
            }

        }catch (e:Exception){
            ErrorUtil.e(e)
        }finally {
            if(fromFile.exists()){
                fromFile.delete()
            }
        }
    }

}