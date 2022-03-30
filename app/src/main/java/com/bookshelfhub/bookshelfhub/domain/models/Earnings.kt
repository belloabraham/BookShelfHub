package com.bookshelfhub.bookshelfhub.domain.models

import androidx.room.Entity
import androidx.room.PrimaryKey

data class Earnings(
    var refereeId:String,
    //Same as User Id
    val referrerId: String,
    val earn: Double
)