package com.bookshelfhub.downloadmanager.request

import com.bookshelfhub.downloadmanager.Priority


class DownloadRequestBuilder(val url: String, val dirPath: String, val fileName: String):RequestBuilder {

    var priority: Priority = Priority.MEDIUM
    var tag: Any? = null
    var readTimeout = 0
    var connectTimeout = 0
    var userAgent: String? = null
    var headerMap: HashMap<String, MutableList<String>>? = null


    override fun setHeader(name: String, value: String): DownloadRequestBuilder {
        if (headerMap == null) {
            headerMap = HashMap()
        }
        var list = headerMap!![name]
        if (list == null) {
            list = ArrayList()
            headerMap!![name] = list
        }
        if (!list.contains(value)) {
            list.add(value)
        }
        return this
    }

    override fun setPriority(priority: Priority): DownloadRequestBuilder {
        this.priority = priority
        return this
    }

    override fun setTag(tag: Any): DownloadRequestBuilder {
        this.tag = tag
        return this
    }

    override fun setReadTimeout(readTimeout: Int): DownloadRequestBuilder {
        this.readTimeout = readTimeout
        return this
    }

    override fun setConnectTimeout(connectTimeout: Int): DownloadRequestBuilder {
        this.connectTimeout = connectTimeout
        return this
    }

    override fun setUserAgent(userAgent: String?): DownloadRequestBuilder {
        this.userAgent = userAgent
        return this
    }

    fun build(): DownloadRequest {
        return DownloadRequest(this)
    }

}