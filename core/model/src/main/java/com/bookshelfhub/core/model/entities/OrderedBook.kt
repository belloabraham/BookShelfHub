package com.bookshelfhub.core.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bookshelfhub.core.model.ISearchResult
import com.bookshelfhub.core.model.uistate.IOrderedBookUiState
import com.google.firebase.Timestamp

@Entity(tableName= "OrderedBooks")
data class OrderedBook(
    @PrimaryKey
    override val bookId:String,
    override val userId:String,
    override val name:String,
    override val coverDataUrl:String,
    override val pubId: String,
    val sellerCurrency:String,
    override val userCountryCode:String?,
    override var transactionReference:String?, //Book receipt
    val dateTime:Timestamp?=null,
    val month: Int,
    val year: Int,
    override val serialNo:Long,
    override val additionInfo: String?,
    val collabCommission: Double?,
    val collabId:String?,
    override val price: Double
): ISearchResult, IOrderedBooks, IOrderedBookUiState