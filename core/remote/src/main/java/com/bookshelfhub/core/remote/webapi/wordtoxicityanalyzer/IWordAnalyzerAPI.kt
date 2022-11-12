package com.bookshelfhub.core.remote.webapi.wordtoxicityanalyzer

import com.bookshelfhub.core.model.apis.perspective.post.PostBody
import com.bookshelfhub.core.model.apis.perspective.response.ResponseBody
import retrofit2.Response

interface IWordAnalyzerAPI {
    suspend fun analyze(postBody: PostBody, key: String): Response<ResponseBody>
}