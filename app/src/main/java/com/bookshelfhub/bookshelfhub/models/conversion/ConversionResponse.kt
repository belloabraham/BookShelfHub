package com.bookshelfhub.bookshelfhub.models.conversion

data class ConversionResponse (
    val success : Boolean,
    val query : Query,
    val info : Info,
    val historical : String,
    val date : String,
    val result : Double
)