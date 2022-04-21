package com.bookshelfhub.bookshelfhub.helpers.webapi.wordtoxicityanalyzer

import com.bookshelfhub.bookshelfhub.data.models.apis.perspective.post.PostBody
import com.bookshelfhub.bookshelfhub.data.models.apis.perspective.response.ResponseBody
import com.bookshelfhub.bookshelfhub.helpers.webapi.retrofit.PerspectiveWordAnalyzerAPI
import retrofit2.Response

class WordAnalyzerAPI(private val perspectiveApi: PerspectiveWordAnalyzerAPI) : IWordAnalyzerAPI {

    override suspend fun analyze(postBody: PostBody, key:String):Response<ResponseBody>{
        return perspectiveApi.analyze(postBody, key)
    }
}