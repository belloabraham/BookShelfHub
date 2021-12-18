package com.bookshelfhub.downloadmanager

interface OnDownloadListener {
    fun onDownloadComplete()

    fun onError(error: Error?)
}