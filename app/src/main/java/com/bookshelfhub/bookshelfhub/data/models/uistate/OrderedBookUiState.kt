package com.bookshelfhub.bookshelfhub.data.models.uistate

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bookshelfhub.bookshelfhub.data.models.ISearchResult
import com.bookshelfhub.bookshelfhub.data.models.entities.IOrderedBooks
import com.google.firebase.Timestamp

@Entity(tableName= "OrderedBooks")
data class OrderedBookUiState(
    @PrimaryKey
    override val bookId:String,
    override val priceInUSD: Double,
    override val userId:String,
    override val name:String,
    override val coverUrl:String,
    override val pubId: String,
    override val referrerId:String?,
    override val orderedCountryCode:String?,
    override var transactionReference:String?,
    override val password:String?=null,
    override val downloadUrl:String?=null,
    override val dateTime:Timestamp?=null,
    override val month: Int,
    override val year: Int,
    override val additionInfo: String?
): ISearchResult, IOrderedBooks