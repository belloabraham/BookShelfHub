package com.bookshelfhub.bookshelfhub.services.payment

import com.bookshelfhub.bookshelfhub.BuildConfig
import com.paymentwall.pwunifiedsdk.core.UnifiedRequest

class PaymentWall(private val projectKey:String, private val currency:String, private val userId:String) {

    private val request = UnifiedRequest()

    fun chargeCard(amount:Double, itemName:String){
        request.setAmount(amount)
        request.setCurrency(currency)
        request.pwProjectKey =projectKey
        request.setTimeout(30000)
        request.itemName = itemName
        request.setUserId(userId)
        request.isTestMode = BuildConfig.DEBUG
    }
}