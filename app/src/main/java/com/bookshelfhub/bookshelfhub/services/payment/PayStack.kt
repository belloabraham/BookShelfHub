package com.bookshelfhub.bookshelfhub.services.payment

import android.app.Activity
import co.paystack.android.Paystack
import co.paystack.android.PaystackSdk
import co.paystack.android.model.Card
import co.paystack.android.model.Charge
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.PaymentCard
import com.bookshelfhub.bookshelfhub.wrappers.Json

class PayStack(paymentCard: PaymentCard, val activity: Activity, val json: Json, private val callBack: Paystack.TransactionCallback,
) : IPayment {

    var card: Card = Card(paymentCard.cardNo, paymentCard.expiryMonth, paymentCard.expiryYear, paymentCard.cvv)

    override fun chargeCard(
        amount:Int,
        userDataKey:String,
        userData:HashMap<String, String>,
        currency:String,
        ) {
        val charge = Charge()
        charge.card = card
        charge.amount = amount
        charge.currency = currency
        charge.putMetadata(userDataKey, json.getJsonObject(userData))
        PaystackSdk.chargeCard(activity, charge, callBack)
    }
}