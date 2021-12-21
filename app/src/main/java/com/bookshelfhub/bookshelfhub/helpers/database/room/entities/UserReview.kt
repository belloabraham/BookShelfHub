package com.bookshelfhub.bookshelfhub.helpers.database.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp


@Entity(tableName = "UserReview")
data class UserReview(
    @PrimaryKey
    val isbn:String,
    var review:String,
    var userRating:Double,
    var userName:String,
    var verified:Boolean,
    var userPhoto:String?,
    var postedBefore:Boolean,
    val dateTime: Timestamp? = null
    )