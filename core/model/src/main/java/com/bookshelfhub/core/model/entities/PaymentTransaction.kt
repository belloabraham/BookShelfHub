package com.bookshelfhub.core.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "PaymentTransactions")
data class PaymentTransaction(
    @PrimaryKey
    override val bookId: String,
    override val userId: String,
    override val name: String,
    override val coverDataUrl: String,
    override val pubId: String,
    val collaboratorsId:String?,
    override val userCountryCode: String?,
    override val additionInfo: String?,
    override val price: Double,
    //Required to determine if the user will earning points or not
    val userCurrency:String?,
    //Currency in which each book was sold just for record purposes
    val sellerCurrency:String
): IOrderedBooks{
    override var transactionReference: String?=null
}