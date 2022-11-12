package com.bookshelfhub.core.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName= "ReadHistories")
data class ReadHistory(
    val isbn:String,
    var lastPageNumber:Int,
    val readPercentage:Int,
    val bookName:String,
    @PrimaryKey
    val id:Int=0
)
