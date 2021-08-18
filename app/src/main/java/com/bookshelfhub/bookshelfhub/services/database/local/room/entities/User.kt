package com.bookshelfhub.bookshelfhub.services.database.local.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "User")
data class User(
     @PrimaryKey
     val userId: String,
     val authType:String,
     var uploaded: Boolean = false,
     var name:String = "",
     var email: String = "",
     var phone:String = "",
     var appVersion:String = "",
     var device:String = "",
     var deviceOs:String = "",
     var referrerId:String? = null,
     var gender: String?=null,
     var dateOfBirth: String?=null,
     var mailOrPhoneVerified: Boolean = false
)
