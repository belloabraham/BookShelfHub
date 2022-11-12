package com.bookshelfhub.core.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "PaymentTransactions")
data class PaymentTransaction(
    @PrimaryKey
    override val bookId: String,
    override val priceInUSD: Double,
    override val userId: String,
    override val name: String,
    override val coverDataUrl: String,
    override val pubId: String,
    val collaboratorsId:String?,
    override val orderedCountryCode: String?,
    override val additionInfo: String?,
    override val priceInBookCurrency: Double,
): IOrderedBooks{
    override var transactionReference: String?=null
}