package com.bookshelfhub.core.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName= "Bookmarks")
data class Bookmark(
    val userId:String,
    val bookId:String,
    val pageNumb:Int,
    val bookName:String,
    val label:String,
    @PrimaryKey(autoGenerate = true)
    override val id:Long=0,
    var deleted:Boolean=false,
    var uploaded:Boolean=false
): IEntityId
