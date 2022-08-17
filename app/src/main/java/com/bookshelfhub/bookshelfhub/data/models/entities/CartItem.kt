package com.bookshelfhub.bookshelfhub.data.models.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName= "CartItems")
data class CartItem(
    var userId:String,
    @PrimaryKey
    val bookId: String,
    val name: String,
    val author:String,
    val pubId:String,
    val coverUrl: String,
    val collaboratorsId:String?,
    val price: Double=0.0,
    val currency:String,
    // I left this variable just in case there will be a need for it in the future for global transaction in USD
    val priceInUsd: Double?
)