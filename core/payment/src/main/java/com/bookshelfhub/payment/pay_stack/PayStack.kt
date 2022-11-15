package com.bookshelfhub.payment.pay_stack

import android.app.Activity
import co.paystack.android.model.Card
import co.paystack.android.Paystack
import co.paystack.android.PaystackSdk
import co.paystack.android.model.Charge
import com.bookshelfhub.core.common.helpers.Json
import com.bookshelfhub.core.model.entities.PaymentCard
import com.bookshelfhub.payment.IPayment

class PayStack(
    private val userDataKey:String,
    private val metaData:HashMap<String, String>,
    private val activity: Activity,
    private val json: Json,
    private val callBack: Paystack.TransactionCallback
) : IPayment {

    override fun chargeCard(
        publicKey:String,
        paymentCard: PaymentCard,
        amount:Double,
        userEmail:String,
        currencyToChargeForBookSale:String,
        ) {
        val card = Card(paymentCard.cardNo, paymentCard.expiryMonth, paymentCard.expiryYear, paymentCard.cvv)
        PaystackSdk.setPublicKey(publicKey)
        val charge = Charge()
        charge.card = card
        charge.amount = (amount * 100).toInt()
        charge.currency = currencyToChargeForBookSale
        charge.email = userEmail
        charge.putMetadata(userDataKey, json.getJsonObject(metaData))
        PaystackSdk.chargeCard(activity, charge, callBack)
    }


}