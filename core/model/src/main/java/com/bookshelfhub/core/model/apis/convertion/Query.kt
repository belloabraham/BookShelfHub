package com.bookshelfhub.core.model.apis.convertion

data class Query(
    val from: String,
    val to: String,
    val amount: Int
)