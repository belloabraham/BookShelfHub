package com.bookshelfhub.bookshelfhub.services.database.local.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bookshelfhub.bookshelfhub.models.ISearchResult

@Entity(tableName= "OrderedBooks")
data class OrderedBooks(
    @PrimaryKey
     override val isbn:String,
     val userId:String,
     val pubId:String,
     val referrerId:String,
     override val title:String,
     val bookCoverUrl:String,
     val orderDateTime:String,
     val orderYearMonth:String,
    val additionalInfo:String?
    ): ISearchResult