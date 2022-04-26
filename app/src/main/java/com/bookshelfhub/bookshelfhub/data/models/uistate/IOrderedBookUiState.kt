package com.bookshelfhub.bookshelfhub.data.models.uistate

interface IOrderedBookUiState {
    val bookId: String
    val name: String
    val coverUrl: String
    val pubId: String
    val referrerId: String?
    val serialNo: Long
}