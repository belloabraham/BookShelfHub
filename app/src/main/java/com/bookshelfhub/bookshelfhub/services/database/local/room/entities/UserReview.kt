package com.bookshelfhub.bookshelfhub.services.database.local.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue


@Entity(tableName = "UserReview")
data class UserReview(
    @PrimaryKey
    val isbn:String,
    var review:String,
    var userRating:Double,
    var userName:String,
    val verified:Boolean,
    var userPhoto:String?,
    var postedBefore:Boolean,
    val dateTime: Timestamp? = null
    )