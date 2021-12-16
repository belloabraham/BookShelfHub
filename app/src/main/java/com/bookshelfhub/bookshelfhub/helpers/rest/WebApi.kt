package com.bookshelfhub.bookshelfhub.helpers.rest

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType

class WebApi() {

   private var apiClient = OkHttpClient()

    fun get(endPointUrl:String, queryParameters:String,onComplete:(Response)->Unit) {
        val url = endPointUrl+queryParameters

        val request = Request.Builder()
            .url(url)
            .build()
        apiClient.newCall(request).execute().use {
            onComplete(it)
        }
    }

    fun post(endPointUrl:String, queryParameter:String, requestBody: RequestBody, onComplete:(Response)->Unit){
        val url = endPointUrl+queryParameter
        val request = Request.Builder()
            .post(requestBody)
            .url(url)
            .build()
        apiClient.newCall(request).execute().use {
            onComplete(it)
        }

    }

}