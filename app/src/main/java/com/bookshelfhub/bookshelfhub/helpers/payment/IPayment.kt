package com.bookshelfhub.bookshelfhub.helpers.payment

import com.bookshelfhub.bookshelfhub.helpers.Currency
import com.bookshelfhub.bookshelfhub.data.models.entities.PaymentCard

interface IPayment {
    fun chargeCard(
        publicKey:String,
        paymentCard: PaymentCard,
        amount:Double,
        currency: String = Currency.USD
    )
}