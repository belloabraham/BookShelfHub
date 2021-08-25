package com.bookshelfhub.bookshelfhub.services.payment

interface IPayment {
    fun chargeCard(
        amount: Int,
        userDataKey:String,
        userData:HashMap<String, String>,
        currency: String=Currency.USD.Value,
    )
}