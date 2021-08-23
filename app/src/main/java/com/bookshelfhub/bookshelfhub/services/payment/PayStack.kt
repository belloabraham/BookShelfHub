package com.bookshelfhub.bookshelfhub.services.payment

import android.app.Activity
import co.paystack.android.Paystack
import co.paystack.android.PaystackSdk
import co.paystack.android.model.Card
import co.paystack.android.model.Charge
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.IPaymentCard

class PayStack(paymentCard: IPaymentCard) {

    var card:Card = Card(paymentCard.cardNo, paymentCard.expiryMonth, paymentCard.expiryYear, paymentCard.cvv)

    fun isValidCard(paymentCard: IPaymentCard): Boolean {
       return Card(paymentCard.cardNo, paymentCard.expiryMonth, paymentCard.expiryYear, paymentCard.cvv).validCVC()
    }

    fun isValidCVC(): Boolean {
        return card.validCVC()
    }

    fun isValidExpDate(): Boolean {
        card.type
        return card.validExpiryDate()
    }

    fun isValidCardNo(): Boolean {
        return card.validNumber()
    }

    fun chargeCard(paymentCard: IPaymentCard, activity: Activity, callBack:Paystack.TransactionCallback){
        val card = Card(paymentCard.cardNo, paymentCard.expiryMonth, paymentCard.expiryYear, paymentCard.cvv)
        val charge = Charge()
        charge.card = card
        PaystackSdk.chargeCard(activity, charge, callBack)
    }
}