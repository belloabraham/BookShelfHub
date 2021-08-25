package com.bookshelfhub.bookshelfhub.services.database.local.room.entities

import com.google.firebase.Timestamp

interface IOrderedBooks {
    val isbn: String
    val title: String
    val userId: String
    val referrerId: String?
    val orderedCountryCode: String?
    val coverUrl: String
    var transactionReference: String?
    val decryptKey: String?
    val password: String?
    val downloadUrl: String?
    val dateTime: Timestamp?
}