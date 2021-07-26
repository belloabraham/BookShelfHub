package com.bookshelfhub.bookshelfhub.services.database.local.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName= "Cart")
data class Cart(
    @PrimaryKey
    var isbn: String
)