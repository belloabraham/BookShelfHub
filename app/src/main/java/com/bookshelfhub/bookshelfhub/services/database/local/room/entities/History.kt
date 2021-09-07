package com.bookshelfhub.bookshelfhub.services.database.local.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName= "History")
data class History(
    @PrimaryKey
    val isbn:String,
    var lastPageNumber:Int,
    val readPercentage:Int,
    val bookName:String
)
