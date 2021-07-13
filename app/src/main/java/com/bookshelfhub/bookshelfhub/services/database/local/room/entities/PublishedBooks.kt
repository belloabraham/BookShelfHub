package com.bookshelfhub.bookshelfhub.services.database.local.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName= "PublishedBooks")
data class PublishedBooks(
    @PrimaryKey
     val isbn: String,
    val pubId: String="",
    val name: String,
    val author: String="",
    val coverUrl: String,
    val description: String="",
    val datePublished: String="",
    val noOfDownloads: Long=0,
    val price: Double=0.0,
    val totalRatings: Double=0.0,
    val category: String,
    val language:String="",
    val copyrightLawUrl: String="",
    val authorEmail: String="",
    val tags: String,
    val trending: Boolean = false
)