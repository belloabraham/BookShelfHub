package com.bookshelfhub.bookshelfhub.services.database.local.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName= "PaymentCard")
data class PaymentCard(
    @PrimaryKey
    override val cardNo: String,
    override val expiryMonth: Int,
    override val expiryYear:Int,
    override val cvv:String,
) : IPaymentCard