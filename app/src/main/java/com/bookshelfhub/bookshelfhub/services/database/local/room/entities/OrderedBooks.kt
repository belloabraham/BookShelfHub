package com.bookshelfhub.bookshelfhub.services.database.local.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bookshelfhub.bookshelfhub.models.ISearchResult
import com.google.firebase.Timestamp

@Entity(tableName= "OrderedBooks")
data class OrderedBooks(
    @PrimaryKey
    override val isbn:String,
    override val priceInUSD: Double,
    override val title:String,
    override val userId:String,
    override val referrerId:String?,
    override val orderedCountryCode:String?,
    override val coverUrl:String,
    override var transactionReference:String?,
    override val password:String?=null,
    override val downloadUrl:String?=null,
    override val dateTime:Timestamp?=null
    ): ISearchResult, IOrderedBooks