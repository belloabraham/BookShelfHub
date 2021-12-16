package com.bookshelfhub.bookshelfhub.helpers

import com.bookshelfhub.bookshelfhub.models.perspective.post.Comment
import com.bookshelfhub.bookshelfhub.models.perspective.post.PostBody
import com.bookshelfhub.bookshelfhub.models.perspective.post.RequestedAttributes
import com.bookshelfhub.bookshelfhub.models.perspective.post.TOXICITY
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class WebApi() {

   private var apiClient = OkHttpClient()

    fun get(queryParameters:String, endPointUrl:String, onComplete:(Response)->Unit) {
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


    companion object {
       // val MEDIA_TYPE_TEXT_MARKDOWN = "text/x-markdown; charset=utf-8".toMediaType()
       val MEDIA_TYPE_APPLICATION_JSON = "application/json; charset=utf-8".toMediaType()
    }


}