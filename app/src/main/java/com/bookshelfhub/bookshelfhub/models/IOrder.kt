package com.bookshelfhub.bookshelfhub.models

interface IOrder {
    val bookIsbn: String
    val userId: String
    val pubId: String
    val bookName: String
    val bookCoverUrl: String
    val orderDateTime: String
    val key: String?
}