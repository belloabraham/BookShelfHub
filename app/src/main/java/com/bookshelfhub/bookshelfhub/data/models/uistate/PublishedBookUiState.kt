package com.bookshelfhub.bookshelfhub.data.models.uistate

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp

data class PublishedBookUiState(
    override val bookId: String,
    override val name: String="",
    override val author: String="",
    override val coverUrl: String="",
) : IPublishedBookUiState