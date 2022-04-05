package com.bookshelfhub.bookshelfhub.data.models.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName= "PaymentCard")
data class PaymentCard(
    @PrimaryKey
    val cardNo: String,
    val expiryMonth: Int,
    val expiryYear:Int,
    val cvv:String,
    var cardType:String="",
    var lastFourDigit:String=""
)
