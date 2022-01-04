package com.bookshelfhub.bookshelfhub.models.convertion

data class Fixer(
    val success: Boolean,
    val query: Query,
    val info: Info,
    val date: String,
    val result: Double
)