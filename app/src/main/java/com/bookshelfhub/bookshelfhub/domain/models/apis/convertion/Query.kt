package com.bookshelfhub.bookshelfhub.domain.models.apis.convertion

data class Query(
    val from: String,
    val to: String,
    val amount: Int
)