package com.bookshelfhub.bookshelfhub.services.database.local.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue

@Entity(tableName= "PublishedBook")
data class PublishedBook(
    @PrimaryKey
     val isbn: String,
    val name: String="",
    val author: String="",
    val coverUrl: String="",
    val totalDownloads: Long=0,
    val description: String="",
    val dateTime: Timestamp? = null,
    val category: String="",
    val tag: String="",
    val totalReviews:Long=0,
    val totalRatings:Float=0f,
    val published:Boolean=true,
    val pubId: String="",
    val language:String="",
    var price: Int=0,
    val recommended:Boolean = false
    ) 