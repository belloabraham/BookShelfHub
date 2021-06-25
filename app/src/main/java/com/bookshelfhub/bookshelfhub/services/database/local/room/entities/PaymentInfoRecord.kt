package com.bookshelfhub.bookshelfhub.services.database.local.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName= "PaymentInfo")
data class PaymentInfoRecord(
     @PrimaryKey
     override val cardNumber:String,
     override val userId:String,
     override val nameOnCard:String,
     override val ccv:String,
     override val address:String,
     override val expDate:String
) : IPaymentInfo