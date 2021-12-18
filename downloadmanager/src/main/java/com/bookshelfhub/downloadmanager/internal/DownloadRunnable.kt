package com.bookshelfhub.downloadmanager.internal

import com.bookshelfhub.downloadmanager.Error
import com.bookshelfhub.downloadmanager.Priority
import com.bookshelfhub.downloadmanager.Response
import com.bookshelfhub.downloadmanager.Status
import com.bookshelfhub.downloadmanager.request.DownloadRequest


class DownloadRunnable(private val request: DownloadRequest):Runnable {

    var priority: Priority = request.getPriority()
    var sequence = 0

    init {
        sequence = request.getSequenceNumber()
    }

    override fun run() {
        request.setStatus(Status.RUNNING)
        val downloadTask: DownloadTask = DownloadTask.create(request)
        val response: Response = downloadTask.run()
        if (response.isSuccessful) {
            request.deliverSuccess()
        } else if (response.isPaused) {
            request.deliverPauseEvent()
        } else if (response.error != null) {
            request.deliverError(response.error!!)
        } else if (!response.isCancelled) {
            request.deliverError(Error())
        }
    }

}