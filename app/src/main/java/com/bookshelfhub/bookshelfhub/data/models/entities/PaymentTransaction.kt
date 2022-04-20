package com.bookshelfhub.bookshelfhub.data.models.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp

@Entity(tableName = "PaymentTransactions")
data class PaymentTransaction(
    @PrimaryKey
    override val bookId: String,
    override val priceInUSD: Double,
    override val userId: String,
    override val name: String,
    override val coverUrl: String,
    override val pubId: String,
    override val referrerId:String?,
    override val orderedCountryCode: String?,
    override var transactionReference: String?=null,
    override val password: String? = null,
    override val downloadUrl: String? = null,
    override val dateTime: Timestamp?=null,
    override val additionInfo: String?,
): IOrderedBooks