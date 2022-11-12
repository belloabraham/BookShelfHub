package com.bookshelfhub.core.model.uistate

data class PublishedBookUiState(
    override val bookId: String,
    override val name: String="",
    override val author: String="",
    override val coverDataUrl: String="",
) : IPublishedBookUiState