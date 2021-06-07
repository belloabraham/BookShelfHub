package com.bookshelfhub.bookshelfhub.services.database.local.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName= "user_table")
data class User(
     @PrimaryKey
     val uId:String,
     val name:String,
     val email: String,
     val phone:String,
     val photoUri:String?,
     val authType:String,
     val appVersion:String,
     val device:String,
     @field:JvmField
     val isUploaded: Boolean = false
)
