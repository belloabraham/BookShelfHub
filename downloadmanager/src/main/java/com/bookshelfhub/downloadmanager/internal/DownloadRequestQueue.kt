package com.bookshelfhub.downloadmanager.internal

import com.bookshelfhub.downloadmanager.Status
import com.bookshelfhub.downloadmanager.core.Core
import com.bookshelfhub.downloadmanager.request.DownloadRequest
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger


class DownloadRequestQueue() {
    private var currentRequestMap: MutableMap<Int, DownloadRequest> = ConcurrentHashMap<Int, DownloadRequest>()
    private var sequenceGenerator: AtomicInteger? = null


    init {
        sequenceGenerator = AtomicInteger()
    }

    companion object{
        private var instance: DownloadRequestQueue? = null

        fun initialize() {
            getInstance()
        }

        fun getInstance(): DownloadRequestQueue {
            if (instance == null) {
                synchronized(DownloadRequestQueue::class.java) {
                    if (instance == null) {
                        instance = DownloadRequestQueue()
                    }
                }
            }
            return instance!!
        }
    }

    private fun getSequenceNumber(): Int {
        return sequenceGenerator!!.incrementAndGet()
    }

    fun pause(downloadId: Int) {
        currentRequestMap[downloadId]?.setStatus(Status.PAUSED)
    }

    fun resume(downloadId: Int) {
        val request: DownloadRequest? = currentRequestMap[downloadId]
        request?.let {
            it.setStatus(Status.QUEUED)
            it.setFuture(
                Core.getInstance()
                    .getExecutorSupplier()
                    .forDownloadTasks()
                    .submit(DownloadRunnable(request))
            )
        }
    }

    private fun cancelAndRemoveFromMap(request: DownloadRequest?) {
        request?.let {
            request.cancel()
            currentRequestMap.remove(request.getDownloadId())
        }
    }

    fun cancel(downloadId: Int) {
        val request: DownloadRequest? = currentRequestMap[downloadId]
        cancelAndRemoveFromMap(request)
    }

    fun cancel(tag: Any?) {
        for ((_, request) in currentRequestMap) {
            if (request.getTag() is String && tag is String) {
                val tempRequestTag = request.getTag() as String
                if (tempRequestTag == tag) {
                    cancelAndRemoveFromMap(request)
                }
            } else if (request.getTag() == tag) {
                cancelAndRemoveFromMap(request)
            }
        }
    }

    fun cancelAll() {
        for ((_, request) in currentRequestMap) {
            cancelAndRemoveFromMap(request)
        }
    }

    fun getStatus(downloadId: Int): Status? {
        val request: DownloadRequest? = currentRequestMap[downloadId]
        return if (request != null) {
            request.getStatus()
        } else Status.UNKNOWN
    }

    fun addRequest(request: DownloadRequest) {
        currentRequestMap[request.getDownloadId()] = request
        request.setStatus(Status.QUEUED)
        request.setSequenceNumber(getSequenceNumber())
        request.setFuture(
            Core.getInstance()
                .getExecutorSupplier()
                .forDownloadTasks()
                .submit(DownloadRunnable(request))
        )
    }

    fun finish(request: DownloadRequest) {
        currentRequestMap.remove(request.getDownloadId())
    }
}