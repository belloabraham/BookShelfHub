package com.bookshelfhub.bookshelfhub

import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bookshelfhub.bookshelfhub.data.Download
import com.bookshelfhub.bookshelfhub.helpers.notification.NotificationBuilder
import com.bookshelfhub.downloadmanager.*
import com.bookshelfhub.downloadmanager.request.DownloadRequest

class BookDownloadService : LifecycleService() {

    companion object {
        private val liveDownloadRequest:MutableLiveData<DownloadRequest> = MutableLiveData()

        private val liveDownloadResult:MutableLiveData<DownloadResult>  = MutableLiveData()

        private var listOfDownloadItems = mutableListOf<DownloadItems>()

        fun getLiveDownloadRequest(): LiveData<DownloadRequest>{
            return liveDownloadRequest
        }

        fun getLiveDownloadResult(): LiveData<DownloadResult> {
            return liveDownloadResult
        }
    }

    data class DownloadItems(
        val id: Int,
        var url: String,
        var hasError:Boolean=false
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


        intent?.let {
            when (it.action) {
                Download.ACTION_START -> {
                    //Called for every new download started
                    val downloadRequest = getDownloadReq(url!!, dirPath!!, fileName!!, bookName!!)
                    startDownload(downloadRequest, bookName)
                    liveDownloadRequest.value = downloadRequest
                    listOfDownloadItems.add(DownloadItems(downloadRequest.getDownloadId(), url))
                }
                Download.ACTION_RESUME -> {
                    //Called for already running downloads
                    DownloadManager.pause(downloadId!!)
                    showNotification(downloadId, bookName!!, R.string.downloading_resuming, true, R.string.resume)
                }
                Download.ACTION_PAUSE -> {
                    //Called for already running downloads
                    DownloadManager.resume(downloadId!!)
                    showNotification(downloadId, bookName!!, R.string.downloading_paused, false, R.string.pause)
                }
                else -> {

                }
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }


    private fun getDownloadReq(url: String, dirPath: String, fileName: String, bookName:String): DownloadRequest {
        return DownloadManager.download(url, dirPath, fileName)
            .build()
            .setOnProgressListener(object : OnProgressListener {

                override fun onProgress(progress: Progress?) {
                    //Show Notification Msg for download progressing
                    progress?.let { mProgress ->
                        if (mProgress.currentBytes > 0) {
                            val percentage = (mProgress.currentBytes / mProgress.totalBytes) * 100
                            val  message =  String.format(getString(R.string.download_percent_msg), percentage)
                            val downloadId = listOfDownloadItems.find {
                                it.url==url
                            }?.id

                            showNotification(downloadId!!, bookName, message, true, R.string.resume)
                        }
                    }
                }

            })
    }

    private fun showNotification(downloadId: Int, title: String, message: String, onGoing: Boolean, actionText: Int) {
        val notificationBuilder = getNotificationBuilder(
            actionText, title, message,
            onGoing = onGoing
        )
        startForeground(downloadId, notificationBuilder.getNotificationBuiler().build())
    }

    private fun startDownload(downloadReq: DownloadRequest, title: String) {
        downloadReq.start(object : OnDownloadListener {
            override fun onDownloadComplete() {
                //Send info to shelf fragment about download state
                val downloadId = downloadReq.getDownloadId()
                liveDownloadResult.value =
                    DownloadResult(downloadReq.getDownloadId(), isComplete = true)

                val downloadItem = listOfDownloadItems.find {
                    it.id == downloadReq.getDownloadId()
                }

                showNotification(downloadId, title, R.string.download_complete, false, R.string.open )

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
                    it.id==downloadId
                }?.hasError=true

                val itemsWithoutError = listOfDownloadItems.filter {
                    !it.hasError
                }

                if(itemsWithoutError.isEmpty()){
                    //Stop foreground service
                    stopForeground(true)
                    stopSelf()
                }else{
                    error?.getConnectionException()?.message?.let {
                        showNotification(downloadId, title, "Download error $it", false, R.string.retry)
                    }
                }
            }
        })

        val message = getString(R.string.download_starting)
        val downloadId = downloadReq.getDownloadId()
        val notificationBuilder = getNotificationBuilder(
             R.string.pause, title, message,
            onGoing = true
        )
        startForeground(downloadId, notificationBuilder.getNotificationBuiler().build())
    }

    private fun showNotification(downloadId: Int, title: String, message: Int, onGoing: Boolean, actionText: Int) {
        val msg = getString(message)
        val notificationBuilder = getNotificationBuilder(
             actionText, title, msg,
            onGoing = onGoing
        )
        startForeground(downloadId, notificationBuilder.getNotificationBuiler().build())
    }


    private fun getNotificationBuilder(
        actionText: Int,
        title: String,
        message: String,
        onGoing: Boolean
    ): NotificationBuilder.Builder {
        return NotificationBuilder(this)
            .setActionText(actionText)
            .setAutoCancel(false)
            .setOngoing(onGoing)
            .setMessage(message)
            .setTitle(title)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .Builder(this)
    }

    private fun getPendingIntent() {

    }

}

