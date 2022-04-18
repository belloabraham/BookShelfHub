package com.bookshelfhub.bookshelfhub.helpers.webapi

import com.bookshelfhub.bookshelfhub.data.models.apis.perspective.post.PostBody
import com.bookshelfhub.bookshelfhub.data.models.apis.perspective.response.ResponseBody
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import retrofit2.Response

class WordAnalyzerAPI(private val perspectiveApi: PerspectiveWordAnalyzerAPI) {

    suspend fun analyze(postBody: PostBody, key:String):Response<ResponseBody>{
        return perspectiveApi.analyze(postBody, key)
    }
}