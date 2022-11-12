package com.bookshelfhub.core.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Users")
data class User(
    @PrimaryKey
    val userId: String,
    val authType:String,
    var uploaded: Boolean = false,
    var firstName:String = "",
    var lastName:String ="",
    var email: String = "",
    var phone:String = "",
    var appVersion:String = "",
    var device:String = "",
    var deviceOs:String = "",
    var referrerId:String? = null,
    var gender: String?=null,
    var dateOfBirth: String?=null,
    var mailOrPhoneVerified: Boolean = false,
    var additionInfo:String?=null
)
