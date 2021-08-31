package com.bookshelfhub.bookshelfhub.services.payment

interface IPayment {
    fun chargeCard(
        publicKey:String,
        userDataKey:String,
        userData:HashMap<String, String>,
        currency: String=Currency.USD.Value
    )
}