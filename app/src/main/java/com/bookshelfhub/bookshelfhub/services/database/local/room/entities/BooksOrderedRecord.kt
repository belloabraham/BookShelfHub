package com.bookshelfhub.bookshelfhub.services.database.local.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bookshelfhub.bookshelfhub.models.IOrder

@Entity(tableName= "BooksOrdered")
data class BooksOrderedRecord(
    @PrimaryKey
    override val bookIsbn:String,
    override val userId:String,
    override val pubId:String,
    override val bookName:String,
    override val bookCoverUrl:String,
    override val orderDateTime:String,
    override val key:String?
) : IOrder