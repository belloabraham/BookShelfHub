package com.bookshelfhub.bookshelfhub.services.database.local.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName= "OrderedBooks")
data class OrderedBooks(
    @PrimaryKey
     val isbn:String,
     val userId:String,
     val pubId:String,
     val referrerId:String,
     val bookName:String,
     val bookCoverUrl:String,
     val orderDateTime:String
     )