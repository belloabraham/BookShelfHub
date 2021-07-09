package com.bookshelfhub.bookshelfhub.services.database.local.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bookshelfhub.bookshelfhub.models.ISearchResult

@Entity(tableName= "ShelfSearchHistory")
data class ShelfSearchHistory(
    @PrimaryKey
    override val isbn:String,
    override val title:String,
    val userId:String,
     val dateTime:String
) : ISearchResult