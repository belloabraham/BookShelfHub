package com.bookshelfhub.bookshelfhub.services.database.local.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bookshelfhub.bookshelfhub.models.IUser

@Entity(tableName= "User")
data class UserRecord(
     @PrimaryKey
     override val userId: String,
     override var name:String,
     override var email: String,
     override var phone:String,
     override var photoUri:String?,
     override val authType:String,
     override val appVersion:String,
     override val device:String,
     override val deviceOs:String,
     override val lastUpdated: String,
     override val mailOrPhoneVerified: Boolean =false,
     override var uploaded: Boolean = false,
) : IUser
