package com.bookshelfhub.bookshelfhub

import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bookshelfhub.bookshelfhub.data.Download
import com.bookshelfhub.bookshelfhub.data.Book
import com.bookshelfhub.bookshelfhub.helpers.notification.NotificationBuilder
import com.bookshelfhub.downloadmanager.*
import com.bookshelfhub.downloadmanager.request.DownloadRequest

class BookDownloadService : LifecycleService() {

    companion object {
        private val liveDownloadRequest: MutableLiveData<DownloadRequest> = MutableLiveData()

        private val liveDownloadResult: MutableLiveData<DownloadResult> = MutableLiveData()

        private var listOfDownloadItems = mutableListOf<DownloadItems>()

        fun getLiveDownloadRequest(): LiveData<DownloadRequest> {
            return liveDownloadRequest
        }

        fun getLiveDownloadResult(): LiveData<DownloadResult> {
            return liveDownloadResult
        }
    }

    data class DownloadItems(
        val id: Int,
        var url: String,
        var hasError: Boolean = false
    )


    data class DownloadResult(
        val id: Int,
        val error: Error? = null,
        val isComplete: Boolean = false
    )

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val url = intent?.getStringExtra(Download.URL)
        val dirPath = intent?.getStringExtra(Download.DIR_PATH)
        val fileName = intent?.getStringExtra(Download.FILE_NAME)
        val downloadId = intent?.getIntExtra(Download.DOWNLOAD_ID, 0)
        val bookName = intent?.getStringExtra(Download.BOOK_NAME)
        val isbn = intent?.getStringExtra(Book.ISBN.KEY)



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

    override fun onDestroy() {
        listOfDownloadItems.clear()
        super.onDestroy()
    }


    private fun getDownloadReq(
        url: String,
        dirPath: String,
        fileName: String,
        bookName: String
    ): DownloadRequest {
        val downloadReq =  DownloadManager.download(url, dirPath, fileName)
            .build()


            downloadReq.setOnProgressListener(object : OnProgressListener {

                override fun onProgress(progress: Progress?) {
                    //Show Notification Msg for download progressing
                    progress?.let { mProgress ->
                        if (mProgress.currentBytes > 0) {
                            val percentage = (mProgress.currentBytes / mProgress.totalBytes) * 100
                            val message =
                                String.format(getString(R.string.download_percent_msg), percentage)
                            val downloadId = downloadReq.getDownloadId()

                            val pendingIntent = getSendCommandToServiceIntent(
                                downloadId,
                                Download.ACTION_PAUSE,
                                bookName
                            )
                            showNotification(
                                downloadId,
                                bookName,
                                message,
                                true,
                                R.string.pause,
                                pendingIntent
                            )
                        }
                    }
                }

            })
        return  downloadReq
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

    private fun startDownload(downloadReq: DownloadRequest, bookName: String, isbn:String) {
        downloadReq.start(object : OnDownloadListener {
            override fun onDownloadComplete() {
                //Send info to shelf fragment about download state
                val downloadId = downloadReq.getDownloadId()
                liveDownloadResult.value =
                    DownloadResult(downloadReq.getDownloadId(), isComplete = true)

                val downloadItem = listOfDownloadItems.find {
                    it.id == downloadReq.getDownloadId()
                }

                val pendingIntent = getOpenBookActivityIntent(downloadId, isbn, bookName)
                showNotification(
                    downloadId,
                    bookName,
                    getString(R.string.download_complete),
                    false,
                    R.string.open,
                    pendingIntent
                )

                downloadItem?.let {
                    listOfDownloadItems.remove(it)
                }

                if (listOfDownloadItems.isEmpty()) {
                    //Stop foreground service
                    stopForeground(true)
                    stopSelf()
                }

            }

            override fun onError(error: Error?) {
                val downloadId = downloadReq.getDownloadId()
                //Send info to shelf fragment about download state
                liveDownloadResult.value =
                    DownloadResult(downloadReq.getDownloadId(), error = error)

                listOfDownloadItems.find {
                    it.id == downloadId
                }?.hasError = true

                val itemsWithoutError = listOfDownloadItems.filter {
                    !it.hasError
                }

                if (itemsWithoutError.isEmpty()) {
                    //Stop foreground service
                    stopForeground(true)
                    stopSelf()
                } else {
                    error?.getConnectionException()?.message?.let {
                        val errorMsg = String.format(getString(R.string.download_error_msg), it)
                        val pendingIntent = getSendCommandToServiceIntent(
                            downloadId,
                            Download.ACTION_RESUME,
                            bookName
                        )
                        showNotification(
                            downloadId,
                            bookName,
                            errorMsg,
                            false,
                            R.string.retry,
                            pendingIntent
                        )
                    }
                }
            }
        })

        val message = getString(R.string.download_starting)
        val downloadId = downloadReq.getDownloadId()
        val notificationBuilder = getNotificationBuilder(
            bookName, message,
            onGoing = true
        )
        startForeground(downloadId, notificationBuilder.getNotificationBuiler().build())
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
        intent.putExtra(Book.ISBN.KEY, isbn)
        intent.putExtra(Book.NAME.KEY, bookName)

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

