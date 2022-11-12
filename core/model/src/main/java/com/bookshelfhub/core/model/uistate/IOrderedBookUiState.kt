package com.bookshelfhub.core.model.uistate

interface IOrderedBookUiState {
    val bookId: String
    val name: String
    val coverDataUrl: String
    val pubId: String
    val serialNo: Long
}