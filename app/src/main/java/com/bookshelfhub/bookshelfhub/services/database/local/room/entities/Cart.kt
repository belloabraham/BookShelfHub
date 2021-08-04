package com.bookshelfhub.bookshelfhub.services.database.local.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName= "Cart")
data class Cart(
    var userId:String,
    @PrimaryKey
    override val isbn: String,
    override val pubId: String="",
    override val name: String,
    override val author: String="",
    override val coverUrl: String,
    override val description: String="",
    override val dateTimePublished: String="",
    override val noOfDownloads: Long=5,
    override val price: Double=0.0,
    override val category: String,
    override val language:String="",
    override val tag: String
): IPublishedBooks