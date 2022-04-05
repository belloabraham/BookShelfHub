package com.bookshelfhub.bookshelfhub.data.models.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp

@Entity(tableName= "PublishedBook")
data class PublishedBook(
    @PrimaryKey
    val isbn: String,
    val name: String="",
    val author: String="",
    val coverUrl: String="",
    val totalDownloads: Long=0,
    val description: String="",
    val publishedDate: Timestamp? = null,
    val category: String="",
    val tag: String="",
    val sellerCurrency:String ="",
    val totalReviews:Long=0,
    val totalRatings:Float=0f,
    val published:Boolean=true,
    val pubId: String="",
    val language:String="",
    var price: Double=0.0,
    val recommended:Boolean = false
    ) 