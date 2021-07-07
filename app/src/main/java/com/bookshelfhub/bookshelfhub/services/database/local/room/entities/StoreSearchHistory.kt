package com.bookshelfhub.bookshelfhub.services.database.local.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName= "StoreSearchHistory")
data class StoreSearchHistory(
     val isbn:String,
     val title:String,
     val userId:String,
    val author:String,
    @PrimaryKey (autoGenerate = true)
     val id:Long = 0
)