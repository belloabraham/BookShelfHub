package com.bookshelfhub.bookshelfhub.services.database.local.room.entities

interface IUser {
    val uId: String
    var name: String
    var email: String
    var phone: String
    var photoUri: String?
    val authType: String
    val appVersion: String
    val device: String
    var uploaded: Boolean
}