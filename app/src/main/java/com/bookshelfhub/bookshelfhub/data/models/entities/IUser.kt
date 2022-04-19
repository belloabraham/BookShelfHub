package com.bookshelfhub.bookshelfhub.data.models.entities

interface IUser {
    val userId: String
    val authType: String
    var uploaded: Boolean
    var fistName: String
    var lastName: String
    var email: String
    var phone: String
    var appVersion: String
    var device: String
    var deviceOs: String
    var referrerId: String?
    var gender: String?
    var dateOfBirth: String?
    var mailOrPhoneVerified: Boolean
    var additionInfo: String?
}