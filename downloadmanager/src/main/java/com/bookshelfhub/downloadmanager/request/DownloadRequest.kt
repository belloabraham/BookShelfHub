package com.bookshelfhub.downloadmanager.request

import com.bookshelfhub.downloadmanager.*
import com.bookshelfhub.downloadmanager.core.Core
import com.bookshelfhub.downloadmanager.internal.ComponentHolder
import com.bookshelfhub.downloadmanager.internal.DownloadRequestQueue
import com.bookshelfhub.downloadmanager.internal.SynchronousCall
import com.bookshelfhub.downloadmanager.utils.Utils
import java.util.concurrent.Future
import java.util.HashMap

class DownloadRequest(builder: DownloadRequestBuilder) {

    private var priority: Priority
    private var tag: Any
    private  var url: String
    private var dirPath: String
    private  var fileName: String
    private var sequenceNumber = 0
    private var future: Future<*>? = null
    private var downloadedBytes: Long = 0
    private var totalBytes: Long = 0
    private var readTimeout = 0
    private var connectTimeout = 0
    private var userAgent: String? = null
    private var onProgressListener: OnProgressListener? = null
    private var onDownloadListener: OnDownloadListener? = null
    private var onStartOrResumeListener: OnStartOrResumeListener? = null
    private var onPauseListener: OnPauseListener? = null
    private var onCancelListener: OnCancelListener? = null
    private var downloadId = 0
    private var headerMap: HashMap<String, MutableList<String>>? = null
    private var status: Status? = null


    init {
        url = builder.url
        dirPath = builder.dirPath
        fileName = builder.fileName
        headerMap = builder.headerMap!!
        priority = builder.priority
        tag = builder.tag!!
        readTimeout =
            if (builder.readTimeout != 0) builder.readTimeout else getReadTimeoutFromConfig()
        connectTimeout =
            if (builder.connectTimeout != 0) builder.connectTimeout else getConnectTimeoutFromConfig()
        userAgent = builder.userAgent
    }


    fun getPriority(): Priority{
        return priority
    }

    fun setPriority(priority: Priority) {
        this.priority = priority
    }

    fun getTag(): Any {
        return tag
    }

    fun setTag(tag: Any) {
        this.tag = tag
    }

    fun getUrl(): String{
        return url
    }

    fun setUrl(url: String) {
        this.url = url
    }

    fun getDirPath(): String {
        return dirPath
    }

    fun setDirPath(dirPath: String) {
        this.dirPath = dirPath
    }

    fun getFileName(): String {
        return fileName
    }

    fun setFileName(fileName: String) {
        this.fileName = fileName
    }

    fun getSequenceNumber(): Int {
        return sequenceNumber
    }

    fun setSequenceNumber(sequenceNumber: Int) {
        this.sequenceNumber = sequenceNumber
    }

    fun getHeaders(): HashMap<String, MutableList<String>>? {
        return headerMap
    }

    fun getFuture(): Future<*>? {
        return future
    }

    fun setFuture(future: Future<*>?) {
        this.future = future
    }

    fun getDownloadedBytes(): Long {
        return downloadedBytes
    }

    fun setDownloadedBytes(downloadedBytes: Long) {
        this.downloadedBytes = downloadedBytes
    }

    fun getTotalBytes(): Long {
        return totalBytes
    }

    fun setTotalBytes(totalBytes: Long) {
        this.totalBytes = totalBytes
    }

    fun getReadTimeout(): Int {
        return readTimeout
    }

    fun setReadTimeout(readTimeout: Int) {
        this.readTimeout = readTimeout
    }

    fun getConnectTimeout(): Int {
        return connectTimeout
    }

    fun setConnectTimeout(connectTimeout: Int) {
        this.connectTimeout = connectTimeout
    }

    fun getUserAgent(): String? {
        if (userAgent == null) {
            userAgent = ComponentHolder.getInstance().getUserAgent()
        }
        return userAgent
    }

    fun setUserAgent(userAgent: String?) {
        this.userAgent = userAgent
    }

    fun getDownloadId(): Int {
        return downloadId
    }

    fun setDownloadId(downloadId: Int) {
        this.downloadId = downloadId
    }

    fun getStatus(): Status? {
        return status
    }

    fun setStatus(status: Status?) {
        this.status = status
    }

    fun getOnProgressListener(): OnProgressListener? {
        return onProgressListener
    }

    fun setOnStartOrResumeListener(onStartOrResumeListener: OnStartOrResumeListener?): DownloadRequest {
        this.onStartOrResumeListener = onStartOrResumeListener
        return this
    }

    fun setOnProgressListener(onProgressListener: OnProgressListener?): DownloadRequest{
        this.onProgressListener = onProgressListener
        return this
    }

    fun setOnPauseListener(onPauseListener: OnPauseListener?): DownloadRequest {
        this.onPauseListener = onPauseListener
        return this
    }

    fun setOnCancelListener(onCancelListener: OnCancelListener?): DownloadRequest {
        this.onCancelListener = onCancelListener
        return this
    }

    fun start(onDownloadListener: OnDownloadListener?): Int {
        this.onDownloadListener = onDownloadListener
        downloadId = Utils.getUniqueId(url, dirPath, fileName)
        DownloadRequestQueue.getInstance().addRequest(this)
        return downloadId
    }

    fun executeSync(): Response{
        downloadId = Utils.getUniqueId(url, dirPath, fileName)
        return SynchronousCall(this).execute()
    }

    fun deliverError(error: Error) {
        if (status !== Status.CANCELLED) {
            setStatus(Status.FAILED)
            Core.getInstance().getExecutorSupplier().forMainThreadTasks()
                .execute(Runnable {
                    if (onDownloadListener != null) {
                        onDownloadListener!!.onError(error)
                    }
                    finish()
                })
        }
    }

    fun deliverSuccess() {
        if (status !== Status.CANCELLED) {
            setStatus(Status.COMPLETED)
            Core.getInstance().getExecutorSupplier().forMainThreadTasks()
                .execute(Runnable {
                    if (onDownloadListener != null) {
                        onDownloadListener!!.onDownloadComplete()
                    }
                    finish()
                })
        }
    }

    fun deliverStartEvent() {
        if (status !== Status.CANCELLED) {
            Core.getInstance().getExecutorSupplier().forMainThreadTasks()
                .execute(Runnable {
                    if (onStartOrResumeListener != null) {
                        onStartOrResumeListener!!.onStartOrResume()
                    }
                })
        }
    }

    fun deliverPauseEvent() {
        if (status !== Status.CANCELLED) {
            Core.getInstance().getExecutorSupplier().forMainThreadTasks()
                .execute(Runnable {
                    if (onPauseListener != null) {
                        onPauseListener!!.onPause()
                    }
                })
        }
    }

    private fun deliverCancelEvent() {
        Core.getInstance().getExecutorSupplier().forMainThreadTasks()
            .execute(Runnable {
                onCancelListener?.onCancel()
            })
    }

    fun cancel() {
        status = Status.CANCELLED
        if (future != null) {
            future!!.cancel(true)
        }
        deliverCancelEvent()
        Utils.deleteTempFileAndDatabaseEntryInBackground(
            Utils.getTempPath(dirPath, fileName),
            downloadId
        )
    }

    private fun finish() {
        destroy()
        DownloadRequestQueue.getInstance().finish(this)
    }

    private fun destroy() {
        onProgressListener = null
        onDownloadListener = null
        onStartOrResumeListener = null
        onPauseListener = null
        onCancelListener = null
    }

    private fun getReadTimeoutFromConfig(): Int {
        return ComponentHolder.getInstance().getReadTimeout()
    }

    private fun getConnectTimeoutFromConfig(): Int {
        return ComponentHolder.getInstance().getConnectTimeout()
    }

}