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
    val collaboratorsId:String?,
    override val orderedCountryCode: String?,
    override val additionInfo: String?,
): IOrderedBooks{
    override var transactionReference: String?=null
}