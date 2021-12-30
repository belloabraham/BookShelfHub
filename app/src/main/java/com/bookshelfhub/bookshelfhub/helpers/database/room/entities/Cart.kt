package com.bookshelfhub.bookshelfhub.helpers.database.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName= "Cart")
data class Cart(
    var userId:String,
    @PrimaryKey
     val isbn: String,
    val title: String,
    val author:String,
    val coverUrl: String,
    val referrerId:String?,
    val price: Double=0.0,
    val currency:String,
    val priceInUsd: Double?
)