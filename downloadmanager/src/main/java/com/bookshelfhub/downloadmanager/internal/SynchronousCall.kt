package com.bookshelfhub.downloadmanager.internal

import com.bookshelfhub.downloadmanager.Response
import com.bookshelfhub.downloadmanager.request.DownloadRequest

class SynchronousCall(val request: DownloadRequest) {

    fun execute(): Response {
        val downloadTask = DownloadTask.create(request)
        return downloadTask.run()
    }

}