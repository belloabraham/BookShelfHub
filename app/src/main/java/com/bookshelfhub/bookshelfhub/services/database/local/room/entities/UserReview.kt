package com.bookshelfhub.bookshelfhub.services.database.local.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.FieldValue


@Entity(tableName = "UserReview")
data class UserReview(
    @PrimaryKey
    val isbn:String,
    var review:String,
    var userRating:Float=0f,
    var userName:String,

    //This name is "lastUpdated" is used as a key for cloud db query @ICloudDb
    var lastUpdated:FieldValue = FieldValue.serverTimestamp()
)