package com.bookshelfhub.bookshelfhub.services.database.local.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName= "PaymentInfo")
data class PaymentInfo(
     @PrimaryKey
      val cardNumber:String,
      val userId:String,
      val nameOnCard:String,
      val ccv:String,
      val address:String,
      val expDate:String
) 