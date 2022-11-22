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
    override var transactionReference:String?, //Book payment receipt
    val dateTime:Timestamp?=null,
    override val serialNo:Long,
    override val additionInfo: String?,
    val collabCommissionInPercent: Double?,
    val collabId:String?,
    override val price: Double,
    //used at the admin dashboard to query book by year and month
    val month:Int = 0,
    val year:Int = 0
): ISearchResult, IOrderedBooks, IOrderedBookUiState