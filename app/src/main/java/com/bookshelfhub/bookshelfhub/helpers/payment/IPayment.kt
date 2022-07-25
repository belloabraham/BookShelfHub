package com.bookshelfhub.bookshelfhub.helpers.payment

import com.bookshelfhub.bookshelfhub.data.models.entities.PaymentCard

interface IPayment {
    fun chargeCard(
        publicKey:String,
        paymentCard: PaymentCard,
        amount:Double,
        userEmail:String,
        currency: String = Currency.USD,
    )
}