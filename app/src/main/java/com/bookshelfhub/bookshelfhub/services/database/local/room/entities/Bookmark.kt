package com.bookshelfhub.bookshelfhub.services.database.local.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName= "Bookmark")
data class Bookmark(
    @PrimaryKey(autoGenerate = true)
    override val id:Long=0,
    val userId:String,
    val isbn:String,
    val pageNumb:Int,
    val title:String,
    var deleted:Boolean=false,
    var uploaded:Boolean=false
):IEntityId
