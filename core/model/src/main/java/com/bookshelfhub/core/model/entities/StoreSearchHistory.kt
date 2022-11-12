package com.bookshelfhub.core.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bookshelfhub.core.model.ISearchResult

@Entity(tableName= "StoreSearchHistories")
data class StoreSearchHistory(
    @PrimaryKey
     override val bookId:String,
    override val name:String,
    val userId:String,
    val author:String,
    val dateTime:String
): ISearchResult