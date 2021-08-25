package com.bookshelfhub.bookshelfhub.helpers.webapi

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

class OkHttp(val url:String, val onComplete:(Response)->Unit) {

   private var client: OkHttpClient = OkHttpClient()

    fun get(endPointValue:String, headerName:String, headerValue:String){
        val request = Request.Builder()
            .url(url+endPointValue)
            .addHeader(headerName, headerValue)
            .build()
        client.newCall(request).execute().use { response ->
            onComplete(response)
        }
    }


/*
    fun post(headerName:String, headerValue:String, json:String, mediaType:MediaType){
        val body = json.toRequestBody(mediaType)
        val request = Request.Builder()
            .url(url)
            .addHeader(headerName, headerValue)
            .post(body)
            .build()
        client.newCall(request).execute().use { response ->
            onComplete(response)
        }
    }
*/
}