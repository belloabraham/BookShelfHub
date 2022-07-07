package com.bookshelfhub.bookshelfhub.data.models.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName= "BookVideos")
data class BookVideo(
    @PrimaryKey
    val bookIdPageNumber:String,
    val bookId:String,
    val pageNumber:Int,
    val link:String
)
