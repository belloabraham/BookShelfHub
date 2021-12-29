package com.bookshelfhub.bookshelfhub.services.payment

object PaymentSDK {

    fun get(countryCode:String?): SDKType? {
        return if(countryCode == SupportedCountries.NIGERIA.COUNTRY_CODE || countryCode == SupportedCountries.GHANA.COUNTRY_CODE || countryCode == SupportedCountries.SOUTH_AFRICA.COUNTRY_CODE ){
            SDKType.PAYSTACK
        }else{
            null
        }
    }

}