package com.bookshelfhub.core.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName= "ReadHistories")
data class ReadHistory(
    var lastPageNumber:Int,
    val readPercentage:Int,
    val bookName:String,
    @PrimaryKey
    val bookId:String
)
