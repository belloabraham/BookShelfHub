package com.bookshelfhub.bookshelfhub.services.wordtoxicity

import com.bookshelfhub.bookshelfhub.models.perspective.post.Comment
import com.bookshelfhub.bookshelfhub.models.perspective.post.PostBody
import com.bookshelfhub.bookshelfhub.models.perspective.post.RequestedAttributes
import com.bookshelfhub.bookshelfhub.models.perspective.post.TOXICITY

class Perspective() {

    fun getPostBody(comment:String, language:String="en"): PostBody {
        val requestAttr = RequestedAttributes(TOXICITY())
        return PostBody(Comment(comment), listOf(language), requestAttr)
    }

}