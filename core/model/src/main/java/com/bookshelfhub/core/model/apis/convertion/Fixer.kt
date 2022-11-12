package com.bookshelfhub.core.model.apis.convertion

data class Fixer(
    val success: Boolean,
    val query: Query,
    val info: Info,
    val date: String,
    val result: Double
)