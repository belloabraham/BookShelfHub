package com.bookshelfhub.bookshelfhub.data.models.uistate

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bookshelfhub.bookshelfhub.data.models.ISearchResult
import com.bookshelfhub.bookshelfhub.data.models.entities.IOrderedBooks
import com.google.firebase.Timestamp

data class OrderedBookUiState(
    override val bookId:String,
    override val name:String,
    override val coverUrl:String,
    override val pubId: String,
    override val referrerId:String?,
    override val serialNo:Long,
) : IOrderedBookUiState, ISearchResult