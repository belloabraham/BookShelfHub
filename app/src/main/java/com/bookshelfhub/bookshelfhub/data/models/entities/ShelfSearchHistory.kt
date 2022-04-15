package com.bookshelfhub.bookshelfhub.data.models.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bookshelfhub.bookshelfhub.data.models.ISearchResult

@Entity(tableName= "ShelfSearchHistory")
data class ShelfSearchHistory(
    @PrimaryKey
    override val bookId:String,
    override val name:String,
    val userId:String,
    val dateTime:String
) : ISearchResult