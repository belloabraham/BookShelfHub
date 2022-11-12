package com.bookshelfhub.payment

import com.bookshelfhub.core.model.entities.PaymentCard

interface IPayment {
    fun chargeCard(
        publicKey:String,
        paymentCard: PaymentCard,
        amount:Double,
        userEmail:String,
        currency: String = Currency.USD,
    )
}