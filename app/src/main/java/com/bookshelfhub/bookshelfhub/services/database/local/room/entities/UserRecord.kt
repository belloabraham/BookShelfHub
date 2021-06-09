package com.bookshelfhub.bookshelfhub.services.database.local.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName= "user")
data class UserRecord(
     @PrimaryKey
     override val uId:String,
     override var name:String,
     override var email: String,
     override var phone:String,
     override var photoUri:String?,
     override val authType:String,
     override val appVersion:String,
     override val device:String,

     override var uploaded: Boolean = false
) : IUser
