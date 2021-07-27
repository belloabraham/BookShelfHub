package com.bookshelfhub.bookshelfhub.services.database.local.room.entities

interface IPublishedBooks {
    val isbn: String
    val pubId: String
    val name: String
    val author: String
    val coverUrl: String
    val description: String
    val dateTimePublished: String
    val noOfDownloads: Long
    val price: Double
    val totalRatings: Double
    val category: String
    val recommended: Boolean
    val language: String
    val copyrightLawUrl: String
    val authorEmail: String
    val tag: String
}