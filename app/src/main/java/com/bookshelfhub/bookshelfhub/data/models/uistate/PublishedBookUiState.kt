package com.bookshelfhub.bookshelfhub.data.models.uistate

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp

@Entity(tableName= "PublishedBooks")
data class PublishedBookUiState(
    @PrimaryKey
    val bookId: String,
    val name: String="",
    val author: String="",
    val coverUrl: String="",
    val totalDownloads: Long=0,
    val description: String="",
    val publishedDate: Timestamp? = null,
    val category: String="",
    val tag: String?="",
    val sellerCurrency:String ="",
    val totalReviews:Long=0,
    val totalRatings:Float=0f,
    val published:Boolean=true,
    val lastUpdated: Timestamp? = null,
    val approved:Boolean = false,
    val pubId: String="",
    var price: Double=0.0,
    val recommended:Boolean = false,
    val serialNo: Long=0
    ) 