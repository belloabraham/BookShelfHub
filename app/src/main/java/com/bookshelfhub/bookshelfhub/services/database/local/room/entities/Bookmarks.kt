package com.bookshelfhub.bookshelfhub.services.database.local.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName= "Bookmarks")
data class Bookmarks(
    @PrimaryKey(autoGenerate = true)
    val id:Long,
    val userId:String,
    val isbn:String,
    val pageNumb:Int,
    val title:String,
    val uploaded:Boolean=false
)
