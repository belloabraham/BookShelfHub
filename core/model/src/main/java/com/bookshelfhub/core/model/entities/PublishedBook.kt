package com.bookshelfhub.core.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bookshelfhub.core.model.uistate.IPublishedBookUiState
import com.google.firebase.Timestamp

@Entity(tableName= "PublishedBooks")
data class PublishedBook(
    @PrimaryKey
    override val bookId: String,
    override val name: String="",
    override val author: String="",
    override val coverDataUrl: String="",
    val totalDownloads: Long=0,
    val description: String="",
    val publishedDate: Timestamp? = null,
    val category: String="",
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
    ): IPublishedBookUiState