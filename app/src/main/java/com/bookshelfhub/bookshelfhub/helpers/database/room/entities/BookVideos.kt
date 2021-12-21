package com.bookshelfhub.bookshelfhub.helpers.database.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName= "BookVideos")
data class BookVideos(
    @PrimaryKey
    val isbnPageNumber:String,
    val isbn:String,
    val pageNumber:Int,
    val link:String

)
