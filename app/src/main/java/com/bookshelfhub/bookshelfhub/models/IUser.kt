package com.bookshelfhub.bookshelfhub.models

import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime

interface IUser {
    val userId:String
    var name: String
    var email: String
    var phone: String
    var photoUri: String?
    val authType: String
    val appVersion: String
    val device: String
    val deviceOs:String
    val lastUpdated: String
    var uploaded: Boolean
}