package com.bookshelfhub.bookshelfhub.services.database.local.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bookshelfhub.bookshelfhub.models.ISearchResult
import com.google.firebase.Timestamp

@Entity(tableName= "OrderedBooks")
data class OrderedBooks(
    @PrimaryKey
     override val isbn:String,
     val userId:String,
     val referrerId:String,
     override val title:String,
     val bookCoverUrl:String,
     val additionalInfo:String?,
     val transaction:String?,
     val country:String,
     val orderDateTime:Timestamp? = null,
    ): ISearchResult