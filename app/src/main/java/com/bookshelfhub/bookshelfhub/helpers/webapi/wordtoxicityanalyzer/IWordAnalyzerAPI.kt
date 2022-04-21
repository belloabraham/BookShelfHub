package com.bookshelfhub.bookshelfhub.helpers.webapi.wordtoxicityanalyzer

import com.bookshelfhub.bookshelfhub.data.models.apis.perspective.post.PostBody
import com.bookshelfhub.bookshelfhub.data.models.apis.perspective.response.ResponseBody
import retrofit2.Response

interface IWordAnalyzerAPI {
    suspend fun analyze(postBody: PostBody, key: String): Response<ResponseBody>
}