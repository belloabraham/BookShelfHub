package com.bookshelfhub.bookshelfhub.helpers.payment

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

}