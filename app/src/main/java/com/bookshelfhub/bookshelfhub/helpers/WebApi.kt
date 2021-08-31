package com.bookshelfhub.bookshelfhub.helpers

import com.bookshelfhub.bookshelfhub.models.RESTHeaders
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class WebApi(private val endPointUrl:String) {

   private var apiClient = OkHttpClient()

    fun get(queryParameters:String, onComplete:(Response)->Unit) {
        val url = endPointUrl+queryParameters

        val request = Request.Builder()
            .url(url)
            .build()
        apiClient.newCall(request).execute().use {
            onComplete(it)
        }
    }


}