package com.bookshelfhub.bookshelfhub.helpers.database.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp

@Entity(tableName = "PaymentTransaction")
data class PaymentTransaction(
    @PrimaryKey
    override val isbn: String,
    override val priceInUSD: Double,
    override val userId: String,
    override val title: String,
    override val coverUrl: String,
    override val referrerId:String?,
    override val orderedCountryCode: String?,
    override var transactionReference: String?=null,
    override val password: String? = null,
    override val downloadUrl: String? = null,
    override val dateTime: Timestamp?=null,
):IOrderedBooks