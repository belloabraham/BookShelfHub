package com.bookshelfhub.bookshelfhub.services.database.local.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName= "Bookmark")
data class Bookmark(
    val userId:String,
    val isbn:String,
    val pageNumb:Int,
    val title:String,
    @PrimaryKey(autoGenerate = true)
    val id:Long=0,
    val uploaded:Boolean=false
)
