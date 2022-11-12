package com.bookshelfhub.core.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bookshelfhub.core.model.ISearchResult

@Entity(tableName= "ShelfSearchHistories")
data class ShelfSearchHistory(
    @PrimaryKey
    override val bookId:String,
    override val name:String,
    val userId:String,
    val dateTime:String
) : ISearchResult