package com.bookshelfhub.bookshelfhub

import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.bookshelfhub.bookshelfhub.data.Download
import com.bookshelfhub.bookshelfhub.helpers.notification.NotificationBuilder
import com.bookshelfhub.downloadmanager.*
import com.bookshelfhub.downloadmanager.request.DownloadRequest

class BookDownloadService : LifecycleService() {

    companion object {
        private val liveDownloadRequest = MutableLiveData<DownloadRequest>()
        private val liveDownloadResult = MutableLiveData<DownloadResult>()

        private val listOfDownloadItems = mutableListOf<DownloadItems>()

        fun getLiveDownloadRequest(): MutableLiveData<DownloadRequest> {
            return liveDownloadRequest
        }

        fun getLiveDownloadResult(): MutableLiveData<DownloadResult> {
            return liveDownloadResult
        }
    }

    data class DownloadItems(
        val id: Int,
        val name: String,
        var hasError:Boolean
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
                    val downloadRequest = getDownloadReq(url!!, dirPath!!, fileName!!)
                    startDownload(downloadRequest)
                    liveDownloadRequest.value = downloadRequest
                }
                Download.ACTION_RESUME -> {
                    //Called for already running downloads
                    resumeDownload(downloadId!!)
                }
                Download.ACTION_PAUSE -> {
                    //Called for already running downloads
                    pauseDownload(downloadId!!)
                }
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }


    private fun getDownloadReq(url: String, dirPath: String, fileName: String): DownloadRequest {

        return DownloadManager.download(url, dirPath, fileName)
            .build()
            .setOnPauseListener(object : OnPauseListener {
                override fun onPause() {
                    //Show Notification Msg for pause
                }
            })
            .setOnProgressListener(object : OnProgressListener {

                override fun onProgress(progress: Progress?) {
                    //Show Notification Msg for download progressing
                    progress?.let { mProgress ->
                        if (mProgress.currentBytes > 0) {
                            val percentage = (mProgress.currentBytes / mProgress.totalBytes) * 100
                        }
                    }
                }

            })
            .setOnStartOrResumeListener(object : OnStartOrResumeListener {
                override fun onStartOrResume() {
                    //Show Notification Start or Resume
                }
            })
    }

    private fun startDownload(downloadReq: DownloadRequest, title: String) {
        downloadReq.start(object : OnDownloadListener {
            override fun onDownloadComplete() {
                //Send info to shelf fragment about download state
                liveDownloadResult.value =
                    DownloadResult(downloadReq.getDownloadId(), isComplete = true)

                val downloadItem = listOfDownloadItems.find {
                    it.id == downloadReq.getDownloadId()
                }

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
                //Send info to shelf fragment about download state
                liveDownloadResult.value =
                    DownloadResult(downloadReq.getDownloadId(), error = error)
                listOfDownloadItems.find {
                    it.id==downloadReq.getDownloadId()
                }?.hasError=true

                val itemsWithoutError = listOfDownloadItems.filter {
                    !it.hasError
                }

                if(itemsWithoutError.isEmpty()){
                    //Stop foreground service
                    stopForeground(true)
                    stopSelf()
                }else{
                    //TODO Show download error notification
                }
            }
        })

        val message = getString(R.string.download_starting)
        val downloadId = downloadReq.getDownloadId()
        val notificationBuilder = getNotificationBuilder(
            downloadId, R.string.pause, title, message,
            onGoing = true
        )
        startForeground(downloadId, notificationBuilder.getNotificationBuiler().build())
    }


    private fun pauseDownload(downloadId: Int, title: String) {
        DownloadManager.pause(downloadId)
        val message = getString(R.string.downloading_paused)
        val notificationBuilder = getNotificationBuilder(
            downloadId, R.string.resume, title, message,
            onGoing = false
        )
        startForeground(downloadId, notificationBuilder.getNotificationBuiler().build())
    }

    private fun resumeDownload(downloadId: Int, title: String) {
        DownloadManager.resume(downloadId)
        val message = getString(R.string.downloading_resuming)
        val notificationBuilder = getNotificationBuilder(
            downloadId, R.string.pause, title, message,
            onGoing = true
        )
        startForeground(downloadId, notificationBuilder.getNotificationBuiler().build())
    }


    private fun getNotificationBuilder(
        notId: Int,
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

