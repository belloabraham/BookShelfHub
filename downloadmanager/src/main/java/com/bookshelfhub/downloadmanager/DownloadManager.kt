package com.bookshelfhub.downloadmanager

import android.content.Context;
import com.bookshelfhub.downloadmanager.core.Core

import com.bookshelfhub.downloadmanager.internal.ComponentHolder
import com.bookshelfhub.downloadmanager.internal.DownloadRequestQueue
import com.bookshelfhub.downloadmanager.request.DownloadRequestBuilder
import com.bookshelfhub.downloadmanager.utils.Utils


object DownloadManager{

    /**
     * Initializes PRDownloader with the default config.
     *
     * @param context The context
     */
    fun initialize(context: Context) {
        initialize(context, DownloadManagerConfig.newBuilder().build())
    }

    /**
     * Initializes PRDownloader with the custom config.
     *
     * @param context The context
     * @param config  The PRDownloaderConfig
     */
    fun initialize(context: Context, config: DownloadManagerConfig) {
        ComponentHolder.getInstance().init(context, config)
        DownloadRequestQueue.initialize()
    }

    /**
     * Method to make download request
     *
     * @param url      The url on which request is to be made
     * @param dirPath  The directory path on which file is to be saved
     * @param fileName The file name with which file is to be saved
     * @return the DownloadRequestBuilder
     */
    fun download(url: String, dirPath: String, fileName: String): DownloadRequestBuilder? {
        return DownloadRequestBuilder(url, dirPath, fileName)
    }

    /**
     * Method to pause request with the given downloadId
     *
     * @param downloadId The downloadId with which request is to be paused
     */
    fun pause(downloadId: Int) {
        DownloadRequestQueue.getInstance().pause(downloadId)
    }

    /**
     * Method to resume request with the given downloadId
     *
     * @param downloadId The downloadId with which request is to be resumed
     */
    fun resume(downloadId: Int) {
        DownloadRequestQueue.getInstance().resume(downloadId)
    }

    /**
     * Method to cancel request with the given downloadId
     *
     * @param downloadId The downloadId with which request is to be cancelled
     */
    fun cancel(downloadId: Int) {
        DownloadRequestQueue.getInstance().cancel(downloadId)
    }

    /**
     * Method to cancel requests with the given tag
     *
     * @param tag The tag with which requests are to be cancelled
     */
    fun cancel(tag: Any?) {
        DownloadRequestQueue.getInstance().cancel(tag)
    }

    /**
     * Method to cancel all requests
     */
    fun cancelAll() {
        DownloadRequestQueue.getInstance().cancelAll()
    }

    /**
     * Method to check the request with the given downloadId is running or not
     *
     * @param downloadId The downloadId with which request status is to be checked
     * @return the running status
     */
    fun getStatus(downloadId: Int): Status? {
        return DownloadRequestQueue.getInstance().getStatus(downloadId)
    }


    /**
     * Method to clean up temporary resumed files which is older than the given day
     *
     * @param days the days
     */
    fun cleanUp(days: Int) {
        Utils.deleteUnwantedModelsAndTempFiles(days)
    }


    /**
     * Shuts PRDownloader down
     */
    fun shutDown() {
        Core.shutDown()
    }

}