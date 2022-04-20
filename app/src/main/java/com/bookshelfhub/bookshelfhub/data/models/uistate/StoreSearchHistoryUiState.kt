package com.bookshelfhub.bookshelfhub.data.models.uistate

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bookshelfhub.bookshelfhub.data.models.ISearchResult

@Entity(tableName= "StoreSearchHistories")
data class StoreSearchHistoryUiState(
    @PrimaryKey
     override val bookId:String,
    override val name:String,
    val userId:String,
    val author:String,
    val dateTime:String
): ISearchResult