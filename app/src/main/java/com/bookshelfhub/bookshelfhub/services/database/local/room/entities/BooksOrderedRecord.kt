package com.bookshelfhub.bookshelfhub.services.database.local.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName= "BooksOrdered")
data class BooksOrderedRecord(
    @PrimaryKey
     val bookIsbn:String,
     val userId:String,
     val pubId:String,
     val bookName:String,
     val bookCoverUrl:String,
     val orderDateTime:String,
     val key:String?
)