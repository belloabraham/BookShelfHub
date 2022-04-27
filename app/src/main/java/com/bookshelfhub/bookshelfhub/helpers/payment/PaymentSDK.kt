package com.bookshelfhub.bookshelfhub.helpers.payment

object PaymentSDK {

    fun get(countryCode:String?): SDKType? {
        return if(countryCode == SupportedCountries.NIGERIA || countryCode == SupportedCountries.GHANA || countryCode == SupportedCountries.UGANDA  || countryCode == SupportedCountries.TANZANIA){
            SDKType.FLUTTER_WAVE
        }else{
            null
        }
    }

}