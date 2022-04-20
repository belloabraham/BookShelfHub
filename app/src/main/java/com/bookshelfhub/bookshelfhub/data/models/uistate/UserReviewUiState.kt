package com.bookshelfhub.bookshelfhub.data.models.uistate

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp


@Entity(tableName = "UserReviews")
data class UserReviewUiState(
    @PrimaryKey
    val bookId:String,
    var review:String,
    var userRating:Double,
    var userName:String,
    var verified:Boolean,
    var userPhoto:String?,
    var postedBefore:Boolean,
    val dateTime: Timestamp? = null
    )