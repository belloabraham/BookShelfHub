package com.bookshelfhub.bookshelfhub.data.models.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bookshelfhub.bookshelfhub.data.models.ISearchResult
import com.bookshelfhub.bookshelfhub.data.models.uistate.IOrderedBookUiState
import com.google.firebase.Timestamp

@Entity(tableName= "OrderedBooks")
data class OrderedBook(
    @PrimaryKey
    override val bookId:String,
    override val priceInUSD: Double,
    override val userId:String,
    override val name:String,
    override val coverUrl:String,
    override val pubId: String,
    override val orderedCountryCode:String?,
    override var transactionReference:String?,
    val dateTime:Timestamp?=null,
    val month: Int,
    val year: Int,
    override val serialNo:Long,
    override val additionInfo: String?,
    val collabCommission: Double?,
    val collabId:String?,
    override val priceInBookCurrency: Double
): ISearchResult, IOrderedBooks, IOrderedBookUiState