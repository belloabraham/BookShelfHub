package com.bookshelfhub.bookshelfhub.data.models.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName= "Bookmarks")
data class Bookmark(
    val userId:String,
    val isbn:String,
    val pageNumb:Int,
    val title:String,
    @PrimaryKey(autoGenerate = true)
    override val id:Long=0,
    var deleted:Boolean=false,
    var uploaded:Boolean=false
): IEntityId
