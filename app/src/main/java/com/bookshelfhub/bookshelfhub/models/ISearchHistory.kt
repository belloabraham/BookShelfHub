package com.bookshelfhub.bookshelfhub.models


interface ISearchHistory {
    val id:Long
    val isbn: String
    val title: String
    val userId: String
}