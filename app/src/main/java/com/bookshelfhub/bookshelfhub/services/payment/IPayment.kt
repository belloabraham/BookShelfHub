package com.bookshelfhub.bookshelfhub.services.payment

import com.bookshelfhub.bookshelfhub.helpers.currencyconverter.Currency

interface IPayment {
    fun chargeCard(
        publicKey:String,
        userDataKey:String,
        userData:HashMap<String, String>,
        currency: String= Currency.USD
    )
}