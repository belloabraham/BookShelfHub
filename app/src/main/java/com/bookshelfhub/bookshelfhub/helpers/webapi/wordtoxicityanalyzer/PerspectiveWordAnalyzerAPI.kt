package com.bookshelfhub.bookshelfhub.helpers.webapi.wordtoxicityanalyzer

import com.bookshelfhub.bookshelfhub.data.PerspectiveAPI
import com.bookshelfhub.bookshelfhub.data.models.apis.perspective.post.PostBody
import com.bookshelfhub.bookshelfhub.data.models.apis.perspective.response.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface PerspectiveWordAnalyzerAPI {

    @POST(PerspectiveAPI.PATH)
    suspend fun analyze(
        @Body() postBody:PostBody,
        @Query(PerspectiveAPI.KEY) keys:String):Response<ResponseBody>
}