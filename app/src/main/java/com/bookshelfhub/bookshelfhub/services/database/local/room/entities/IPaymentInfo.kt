package com.bookshelfhub.bookshelfhub.services.database.local.room.entities

interface IPaymentInfo {
    val nameOnCard: String
    val cardNumber: String
    val userId: String
    val ccv: String
    val address: String
    val expDate: String
}