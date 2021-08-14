package com.bookshelfhub.bookshelfhub.services.database.local.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "UserReview")
data class UserReview(
    @PrimaryKey
    val isbn:String,
    var review:String,
    var userRating:Float=0f,
    var userName:String
)