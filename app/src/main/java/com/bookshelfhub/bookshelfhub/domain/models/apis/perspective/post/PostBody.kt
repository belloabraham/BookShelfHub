package com.bookshelfhub.bookshelfhub.domain.models.apis.perspective.post

data class PostBody(
    val comment: Comment,
    val languages: List<String>,
    val requestedAttributes: RequestedAttributes
)

data class Comment(
    val text: String
)

data class RequestedAttributes(
    val TOXICITY: TOXICITY
)

class TOXICITY()