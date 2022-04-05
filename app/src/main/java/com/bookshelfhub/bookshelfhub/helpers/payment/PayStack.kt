package com.bookshelfhub.bookshelfhub.helpers.payment
/*
import android.app.Activity
import co.paystack.android.model.Card
import co.paystack.android.Paystack
import co.paystack.android.PaystackSdk
import co.paystack.android.model.Charge
import com.bookshelfhub.bookshelfhub.helpers.Json
import com.bookshelfhub.bookshelfhub.data.models.entities.PaymentCard

class PayStack(
    private val userDataKey:String,
    private val userData:HashMap<String, String>,
    private val activity: Activity, private val json: Json,
    private val callBack: Paystack.TransactionCallback
) : IPayment {

    override fun chargeCard(
        publicKey:String,
        paymentCard: PaymentCard,
        amount:Double,
        currency:String
    ) {
        val card = Card(paymentCard.cardNo, paymentCard.expiryMonth, paymentCard.expiryYear, paymentCard.cvv)
        PaystackSdk.setPublicKey(publicKey)
        val charge = Charge()
        charge.card = card
        charge.amount = (amount*100).toInt()
        charge.currency = currency
        charge.putMetadata(userDataKey, json.getJsonObject(userData))
        PaystackSdk.chargeCard(activity, charge, callBack)
    }


}*/