package com.bookshelfhub.bookshelfhub.helpers.rest

import androidx.work.ListenableWorker
import okhttp3.*

class WebApi() {

   private var apiClient = OkHttpClient()

    fun get(endPointUrl:String, queryParameters:String, onComplete:(Response)->Unit) {
        val url = endPointUrl+queryParameters
        val request = Request.Builder()
            .url(url)
            .build()
        apiClient.newCall(request).execute().use {
            onComplete(it)
        }
    }

      fun post(endPointUrl:String, queryParameter:String, requestBody: RequestBody): Response {
        val url = endPointUrl+queryParameter
        val request = Request.Builder()
            .post(requestBody)
            .url(url)
            .build()
        return apiClient.newCall(request).execute()
    }

}