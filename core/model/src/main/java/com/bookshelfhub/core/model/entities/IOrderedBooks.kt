package com.bookshelfhub.core.model.entities

/**
 * Used to make sure Transaction and Ordered books have the same fields and books purchased bu user will be sent as Payment Transaction
 * to the cloud for payment verification but downloaded as ordered books to the device
 */
interface IOrderedBooks {
    val bookId: String
    // I left this variable just in case there will be a need for it in the future for global transaction in USD
    val priceInUSD: Double
    val priceInBookCurrency:Double
    val userId: String
    val name: String
    val coverDataUrl: String
    val pubId:String
    val orderedCountryCode: String?
    var transactionReference: String?
    val additionInfo:String?
}