package com.bookshelfhub.payment

import android.content.Context
import co.paystack.android.PaystackSdk

object PaymentSDK {

    fun getType(countryCode:String?): PaymentSDKType? {
        return if(
            countryCode == PaystackSupportedCountriesCode.NIGERIA ||
            countryCode == PaystackSupportedCountriesCode.SOUTH_AFRICA ||
            countryCode == PaystackSupportedCountriesCode.GHANA
        ){
            PaymentSDKType.PAYSTACK
        }else{
            null
        }
    }

    fun initialize(context:Context){
        PaystackSdk.initialize(context)
    }

}