package com.bookshelfhub.payment

import android.content.Context
import co.paystack.android.PaystackSdk

object PaymentSDK {

    fun getType(countryCode:String?): PaymentSDKType? {

        return if(
            countryCode == SupportedCountries.NIGERIA || countryCode == SupportedCountries.GHANA
           // countryCode == PaystackSupportedCountriesCode.SOUTH_AFRICA || Restricted by GooglePay Support For Paystack
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