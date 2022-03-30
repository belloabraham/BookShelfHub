package com.bookshelfhub.bookshelfhub.domain.models.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName= "History")
data class History(
    val isbn:String,
    var lastPageNumber:Int,
    val readPercentage:Int,
    val bookName:String,
    @PrimaryKey
    val id:Int=0
)
