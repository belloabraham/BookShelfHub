package com.bookshelfhub.bookshelfhub.services.database.local.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName= "Cart")
data class Cart(
    var userId:String,
    @PrimaryKey
     val isbn: String,
     val name: String,
     val author:String,
     val coverUrl: String,
     val price: Float=0.0f,
)