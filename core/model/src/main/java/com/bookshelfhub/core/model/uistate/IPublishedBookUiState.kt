package com.bookshelfhub.core.model.uistate

interface IPublishedBookUiState {
    val bookId: String
    val name: String
    val author: String
    val coverDataUrl: String
}