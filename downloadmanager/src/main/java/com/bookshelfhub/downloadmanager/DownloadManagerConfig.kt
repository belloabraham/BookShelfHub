package com.bookshelfhub.downloadmanager

import com.bookshelfhub.downloadmanager.httpclient.DefaultHttpClient
import com.bookshelfhub.downloadmanager.httpclient.HttpClient


class DownloadManagerConfig(builder: Builder) {

    private var readTimeout = 0
    private var connectTimeout = 0
    private var userAgent: String? = null
    private var httpClient: HttpClient
    private var databaseEnabled = false

    init {
        readTimeout = builder.readTimeout
        connectTimeout = builder.connectTimeout
        userAgent = builder.userAgent
        httpClient = builder.httpClient
        databaseEnabled = builder.databaseEnabled
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
        return userAgent
    }

    fun setUserAgent(userAgent: String?) {
        this.userAgent = userAgent
    }

    fun getHttpClient(): HttpClient {
        return httpClient
    }

    fun setHttpClient(httpClient: HttpClient) {
        this.httpClient = httpClient
    }

    fun isDatabaseEnabled(): Boolean {
        return databaseEnabled
    }

    fun setDatabaseEnabled(databaseEnabled: Boolean) {
        this.databaseEnabled = databaseEnabled
    }


    companion object{
        fun newBuilder(): Builder {
            return Builder()
        }

        class Builder {
            var readTimeout = Constants.DEFAULT_READ_TIMEOUT_IN_MILLS
            var connectTimeout = Constants.DEFAULT_CONNECT_TIMEOUT_IN_MILLS
            var userAgent = Constants.DEFAULT_USER_AGENT
            var httpClient: HttpClient = DefaultHttpClient()
            var databaseEnabled = false
            fun setReadTimeout(readTimeout: Int): Builder {
                this.readTimeout = readTimeout
                return this
            }

            fun setConnectTimeout(connectTimeout: Int): Builder {
                this.connectTimeout = connectTimeout
                return this
            }

            fun setUserAgent(userAgent: String): Builder {
                this.userAgent = userAgent
                return this
            }

            fun setHttpClient(httpClient: HttpClient): Builder {
                this.httpClient = httpClient
                return this
            }

            fun setDatabaseEnabled(databaseEnabled: Boolean): Builder {
                this.databaseEnabled = databaseEnabled
                return this
            }

            fun build(): DownloadManagerConfig {
                return DownloadManagerConfig(this)
            }
        }
    }

}