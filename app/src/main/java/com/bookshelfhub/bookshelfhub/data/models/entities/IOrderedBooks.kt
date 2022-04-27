package com.bookshelfhub.bookshelfhub.data.models.entities

/**
 * Used to make sure Transaction and Ordered books have the same fields and books purchased bu user will be sent as Payment Transaction
 * to the cloud for payment verification but downloaded as ordered books to the device
 */
interface IOrderedBooks {
    val bookId: String
    val priceInUSD: Double
    val userId: String
    val name: String
    val coverUrl: String
    val pubId:String
    val orderedCountryCode: String?
    var transactionReference: String?
    val additionInfo:String?
    val dateTime: Any?
}