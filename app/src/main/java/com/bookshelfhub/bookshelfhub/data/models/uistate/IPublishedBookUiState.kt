package com.bookshelfhub.bookshelfhub.data.models.uistate

interface IPublishedBookUiState {
    @PrimaryKey
    val bookId: String
    val name: String = ""
    val author: String = ""
    val coverUrl: String = ""
}