package com.bookshelfhub.bookshelfhub.services

import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bookshelfhub.bookshelfhub.BookActivity
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.data.Download
import com.bookshelfhub.bookshelfhub.data.Book
import com.bookshelfhub.bookshelfhub.helpers.notification.NotificationBuilder
import com.bookshelfhub.downloadmanager.*
import com.bookshelfhub.downloadmanager.request.DownloadRequest

class BookDownloadService : LifecycleService() {


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val url = intent?.getStringExtra(Download.URL)
        val dirPath = intent?.getStringExtra(Download.DIR_PATH)
        val fileName = intent?.getStringExtra(Download.FILE_NAME)
        val downloadId = intent?.getIntExtra(Download.DOWNLOAD_ID, 0)
        val bookName = intent?.getStringExtra(Download.BOOK_NAME)
        val isbn = intent?.getStringExtra(Book.ID)



        intent?.let {


            when (it.action) {
                Download.ACTION_START -> {
                    //Called for every new download started
                    val downloadRequest = getDownloadReq(url!!, dirPath!!, fileName!!, bookName!!)
                    startDownload(downloadRequest, bookName, isbn!!)
                    liveDownloadRequest.value = downloadRequest
                    listOfDownloadItems.add(DownloadItems(downloadRequest.getDownloadId(), url))
                }
                Download.ACTION_RESUME -> {
                    //Called for already running downloads
                    DownloadManager.resume(downloadId!!)
                    val pendingIntent =
                        getSendCommandToServiceIntent(downloadId, Download.ACTION_PAUSE, bookName!!)
                    showNotification(
                        downloadId,
                        bookName,
                        getString(R.string.downloading_resuming),
                        true,
                        R.string.pause,
                        pendingIntent
                    )
                }
                Download.ACTION_PAUSE -> {
                    //Called for already running downloads
                    DownloadManager.pause(downloadId!!)
                    val pendingIntent = getSendCommandToServiceIntent(
                        downloadId,
                        Download.ACTION_RESUME,
                        bookName!!
                    )
                    showNotification(
                        downloadId,
                        bookName,
                        getString(R.string.downloading_paused),
                        false,
                        R.string.resume,
                        pendingIntent
                    )
                }
                else -> {}
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }


    private fun showNotification(
        downloadId: Int, title: String,
        message: String, onGoing: Boolean,
        actionText: Int, pendingIntent: PendingIntent?
    ) {
        val notificationBuilder = getNotificationBuilder(
            title, message,
            onGoing = onGoing
        ).getNotificationBuiler()

        pendingIntent?.let {
            val action = getString(actionText)
            notificationBuilder.addAction(0, action, it)
        }

        startForeground(downloadId, notificationBuilder.build())
    }


    private fun getNotificationBuilder(
        title: String,
        message: String,
        onGoing: Boolean,
    ): NotificationBuilder.Builder {
        return NotificationBuilder(this)
            .setAutoCancel(false)
            .setOngoing(onGoing)
            .setMessage(message)
            .setTitle(title)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .Builder(this)
    }


    private fun getOpenBookActivityIntent(downloadId: Int, isbn: String, bookName: String): PendingIntent? {
        val intent = Intent(this, BookActivity::class.java)
        intent.putExtra(Book.ID, isbn)
        intent.putExtra(Book.NAME, bookName)

        return PendingIntent.getService(
            this, downloadId, intent, NotificationBuilder.getIntentFlag()
        )
    }

    private fun getSendCommandToServiceIntent(
        downloadId: Int,
        action: String,
        bookName: String
    ): PendingIntent? {
        val intent = Intent(this, BookDownloadService::class.java)
        intent.action = action
        intent.putExtra(Download.BOOK_NAME, bookName)
        intent.putExtra(Download.DOWNLOAD_ID, downloadId)

        return PendingIntent.getService(
            this, downloadId, intent, 0
        )
    }


}

