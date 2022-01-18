package com.bookshelfhub.bookshelfhub.helpers.database.room.entities

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
