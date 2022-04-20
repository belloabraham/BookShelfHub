package com.bookshelfhub.bookshelfhub.data.models.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName= "CartItems")
data class CartItem(
    var userId:String,
    @PrimaryKey
    val isbn: String,
    val title: String,
    val author:String,
    val pubId:String,
    val coverUrl: String,
    val referrerId:String?,
    val price: Double=0.0,
    val currency:String,
    val priceInUsd: Double?
)