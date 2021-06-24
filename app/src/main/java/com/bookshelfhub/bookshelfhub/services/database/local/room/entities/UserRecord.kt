package com.bookshelfhub.bookshelfhub.services.database.local.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bookshelfhub.bookshelfhub.models.IUser

@Entity(tableName= "User")
data class UserRecord(
     @PrimaryKey
     override val userId: String,
     override val authType:String
) : IUser{
     override var name:String = ""
          get() = field
     set(value){field=value}
     override var email: String = ""
          get() = field
          set(value){field=value}
     override var phone:String = ""
          get() = field
          set(value){field=value}
     override var photoUri:String? = null
          get() = field
          set(value){field=value}
     override var appVersion:String = ""
          get() = field
          set(value){field=value}
     override var device:String = ""
          get() = field
          set(value){field=value}
     override var deviceOs:String = ""
          get() = field
          set(value){field=value}
     override var lastUpdated: String = ""
          get() = field
          set(value){field=value}
     override var mailOrPhoneVerified: Boolean =false
          get() = field
          set(value){field=value}
     override var uploaded: Boolean = false
          get() = field
          set(value){field=value}
}
