package com.bookshelfhub.bookshelfhub.services.database.local.room.entities

import com.google.firebase.Timestamp

/**
 * Used to make sure Transaction and Ordered books have the same fields and books purchased bu user will be sent as Payment Transaction
 * to the cloud for payment verification but downloaded as ordered books to the device
 */
interface IOrderedBooks {
    val isbn: String
    val priceInUSD: Double
    val title: String
    val userId: String
    val referrerId: String?
    val orderedCountryCode: String?
    val coverUrl: String
    var transactionReference: String?
    val password: String?
    val downloadUrl: String?
    val dateTime: Timestamp?
}