package com.bookshelfhub.downloadmanager.httpclient

import com.bookshelfhub.downloadmanager.Constants
import com.bookshelfhub.downloadmanager.request.DownloadRequest
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLConnection
import java.util.*


class DefaultHttpClient:HttpClient {

    private var connection: URLConnection? = null

    override fun clone(): HttpClient {
        return DefaultHttpClient()
    }

    @Throws(IOException::class)
    override fun connect(request: DownloadRequest) {
        connection = URL(request.getUrl()).openConnection()
        connection?.setReadTimeout(request.getReadTimeout())
        connection?.setConnectTimeout(request.getConnectTimeout())
        val range = java.lang.String.format(
            Locale.ENGLISH,
            "bytes=%d-", request.getDownloadedBytes()
        )
        connection?.addRequestProperty(Constants.RANGE, range)
        connection?.addRequestProperty(Constants.USER_AGENT, request.getUserAgent())
        addHeaders(request)
        connection?.connect()
    }

    @Throws(IOException::class)
    override fun getResponseCode(): Int {
        var responseCode = 0
        if (connection is HttpURLConnection) {
            responseCode = (connection as HttpURLConnection).responseCode
        }
        return responseCode
    }

    @Throws(IOException::class)
    override fun getInputStream(): InputStream? {
        return connection!!.getInputStream()
    }

    override fun getContentLength(): Long {
        val length = connection!!.getHeaderField("Content-Length")
        return try {
            length.toLong()
        } catch (e: NumberFormatException) {
            -1
        }
    }

    override fun getResponseHeader(name: String?): String? {
        return connection!!.getHeaderField(name)
    }

    override fun close() {
        // no operation
    }

    override fun getHeaderFields(): Map<String, List<String>> {
        return connection!!.headerFields
    }

    override fun getErrorStream(): InputStream? {
        return if (connection is HttpURLConnection) {
            (connection as HttpURLConnection).errorStream
        } else null
    }

    private fun addHeaders(request: DownloadRequest) {
        val headers: HashMap<String, MutableList<String>>? = request.getHeaders()
        if (headers != null) {
            val entries: Set<Map.Entry<String, List<String>?>> = headers.entries
            for ((name, list) in entries) {
                if (list != null) {
                    for (value in list) {
                        connection!!.addRequestProperty(name, value)
                    }
                }
            }
        }
    }

}