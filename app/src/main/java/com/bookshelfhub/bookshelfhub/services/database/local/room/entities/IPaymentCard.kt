package com.bookshelfhub.bookshelfhub.services.database.local.room.entities

interface IPaymentCard {
    @PrimaryKey
    val cardNo: String
    val expiryMonth: Int
    val expiryYear: Int
    val cvv: String
}