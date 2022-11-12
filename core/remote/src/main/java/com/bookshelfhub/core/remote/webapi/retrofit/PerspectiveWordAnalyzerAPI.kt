package com.bookshelfhub.core.remote.webapi.retrofit

import com.bookshelfhub.core.model.apis.perspective.post.PostBody
import com.bookshelfhub.core.model.apis.perspective.response.ResponseBody
import com.bookshelfhub.core.remote.webapi.PerspectiveAPI
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface PerspectiveWordAnalyzerAPI {

    @Headers("Content-Type: application/json")
    @POST(PerspectiveAPI.PATH)
    suspend fun analyze(
        @Body() postBody: PostBody,
        @Query(PerspectiveAPI.KEY) keys:String):Response<ResponseBody>
}