package com.bookshelfhub.core.remote.webapi.wordtoxicityanalyzer

import com.bookshelfhub.core.model.apis.perspective.post.PostBody
import com.bookshelfhub.core.model.apis.perspective.response.ResponseBody
import com.bookshelfhub.core.remote.webapi.retrofit.PerspectiveWordAnalyzerAPI
import retrofit2.Response

class WordAnalyzerAPI(private val perspectiveApi: PerspectiveWordAnalyzerAPI) : IWordAnalyzerAPI {

    override suspend fun analyze(postBody: PostBody, key:String):Response<ResponseBody>{
        return perspectiveApi.analyze(postBody, key)
    }
}