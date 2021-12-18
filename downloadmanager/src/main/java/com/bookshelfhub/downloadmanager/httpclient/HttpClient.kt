package com.bookshelfhub.downloadmanager.httpclient

import com.bookshelfhub.downloadmanager.request.DownloadRequest
import java.io.IOException
import java.io.InputStream

interface HttpClient : Cloneable {

    public override fun clone(): HttpClient

    @Throws(IOException::class)
    fun connect(request: DownloadRequest)

    @Throws(IOException::class)
    fun getResponseCode(): Int

    @Throws(IOException::class)
    fun getInputStream(): InputStream?

    fun getContentLength(): Long

    fun getResponseHeader(name: String?): String?

    fun close()

    fun getHeaderFields(): Map<String, List<String>>?

    @Throws(IOException::class)
    fun getErrorStream(): InputStream?

}