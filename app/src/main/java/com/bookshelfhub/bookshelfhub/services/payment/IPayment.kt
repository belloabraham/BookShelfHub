package com.bookshelfhub.bookshelfhub.services.payment

import com.bookshelfhub.bookshelfhub.helpers.currencyconverter.Currency
import com.bookshelfhub.bookshelfhub.helpers.database.room.entities.PaymentCard

interface IPayment {
    fun chargeCard(
        publicKey:String,
        paymentCard: PaymentCard,
        amount:Double,
        currency: String = Currency.USD
    )
}