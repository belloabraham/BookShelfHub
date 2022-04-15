package com.bookshelfhub.bookshelfhub.data.models.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bookshelfhub.bookshelfhub.data.models.ISearchResult

@Entity(tableName= "StoreSearchHistory")
data class StoreSearchHistory(
    @PrimaryKey
     override val bookId:String,
    override val name:String,
    val userId:String,
    val author:String,
    val dateTime:String
): ISearchResult