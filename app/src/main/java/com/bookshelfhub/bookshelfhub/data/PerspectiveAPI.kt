package com.bookshelfhub.bookshelfhub.data

import com.bookshelfhub.bookshelfhub.data.models.apis.perspective.post.Comment
import com.bookshelfhub.bookshelfhub.data.models.apis.perspective.post.PostBody
import com.bookshelfhub.bookshelfhub.data.models.apis.perspective.post.RequestedAttributes
import com.bookshelfhub.bookshelfhub.data.models.apis.perspective.post.TOXICITY

object PerspectiveAPI {
    const val PATH="v1alpha1/comments:analyze"
    const val KEY ="key"
    const val ENDPOINT = "https://commentanalyzer.googleapis.com"

    fun getPostBody(words:String, language:String="en"): PostBody {
        val requestAttr = RequestedAttributes(TOXICITY())
        return PostBody(Comment(words), listOf(language), requestAttr)
    }
}