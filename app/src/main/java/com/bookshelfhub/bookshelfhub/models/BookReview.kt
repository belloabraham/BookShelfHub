package com.bookshelfhub.bookshelfhub.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.FieldValue

data class BookReview(
    val isbn:String,
    var review:String,
    var reviewDate:FieldValue,
    var userRating:Float=0f,
    var userName:String,

)