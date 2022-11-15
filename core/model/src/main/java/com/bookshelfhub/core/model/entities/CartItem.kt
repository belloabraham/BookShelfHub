package com.bookshelfhub.core.model.entities

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
    val coverDataUrl: String,
    val collaboratorsId:String?,
    val price: Double=0.0,
    val sellerCurrency:String
)