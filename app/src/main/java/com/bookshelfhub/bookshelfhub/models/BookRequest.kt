package com.bookshelfhub.bookshelfhub.models

data class BookRequest(
    override val title:String,
    override val isbn:String
):ISearchResult
