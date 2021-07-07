package com.bookshelfhub.bookshelfhub.services.database.local.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName= "ShelfSearchHistory")
data class ShelfSearchHistory(
     val isbn:String,
     val title:String,
     val userId:String,
    @PrimaryKey (autoGenerate = true)
     val id:Long =0
)