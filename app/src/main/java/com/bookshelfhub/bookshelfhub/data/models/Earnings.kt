package com.bookshelfhub.bookshelfhub.data.models

data class Earnings(
    var refereeId:String,
    //Same as User Id
    val referrerId: String,
    val earn: Double
)