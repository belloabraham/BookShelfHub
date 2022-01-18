package com.bookshelfhub.downloadmanager.internal

import android.content.Context
import com.bookshelfhub.downloadmanager.Constants
import com.bookshelfhub.downloadmanager.DownloadManager
import com.bookshelfhub.downloadmanager.DownloadManagerConfig
import com.bookshelfhub.downloadmanager.database.*
import com.bookshelfhub.downloadmanager.httpclient.DefaultHttpClient
import com.bookshelfhub.downloadmanager.httpclient.HttpClient


class ComponentHolder {

    private var readTimeout = 0
    private var connectTimeout = 0
    private var userAgent: String? = null
    private  var httpClient: HttpClient?=null
    private var dbHelper: DbHelper? = null

    companion object{
        private val INSTANCE = ComponentHolder()
        fun getInstance(): ComponentHolder {
            return INSTANCE
        }
    }

    fun init(context: Context, config: DownloadManagerConfig) {
        readTimeout = config.getReadTimeout()
        connectTimeout = config.getConnectTimeout()
        userAgent = config.getUserAgent()
        httpClient = config.getHttpClient()
        dbHelper = if (config.isDatabaseEnabled())  AppDbHelper(context) else  NoOpsDbHelper()
        if (config.isDatabaseEnabled()) {
            DownloadManager.cleanUp(10)
        }
    }

    fun getReadTimeout(): Int {
        if (readTimeout == 0) {
            synchronized(ComponentHolder::class.java) {
                if (readTimeout == 0) {
                    readTimeout = Constants.DEFAULT_READ_TIMEOUT_IN_MILLS
                }
            }
        }
        return readTimeout
    }

    fun getConnectTimeout(): Int {
        if (connectTimeout == 0) {
            synchronized(ComponentHolder::class.java) {
                if (connectTimeout == 0) {
                    connectTimeout = Constants.DEFAULT_CONNECT_TIMEOUT_IN_MILLS
                }
            }
        }
        return connectTimeout
    }

    fun getUserAgent(): String? {
        if (userAgent == null) {
            synchronized(ComponentHolder::class.java) {
                if (userAgent == null) {
                    userAgent = Constants.DEFAULT_USER_AGENT
                }
            }
        }
        return userAgent
    }

    fun getDbHelper(): DbHelper{
        if (dbHelper == null) {
            synchronized(ComponentHolder::class.java) {
                if (dbHelper == null) {
                    dbHelper = NoOpsDbHelper()
                }
            }
        }
        return dbHelper!!
    }

    fun getHttpClient(): HttpClient{
        if (httpClient == null) {
            synchronized(ComponentHolder::class.java) {
                if (httpClient == null) {
                    httpClient = DefaultHttpClient()
                }
            }
        }
        return httpClient!!.clone()
    }

}