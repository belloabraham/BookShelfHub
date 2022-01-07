package com.bookshelfhub.bookshelfhub.services.payment

object PaymentSDK {

    fun get(countryCode:String?): SDKType? {
        return if(countryCode == SupportedCountries.NIGERIA.COUNTRY_CODE || countryCode == SupportedCountries.GHANA.COUNTRY_CODE || countryCode == SupportedCountries.UGANDA.COUNTRY_CODE  || countryCode == SupportedCountries.TANZANIA.COUNTRY_CODE){
            SDKType.FLUTTER_WAVE
        }else{
            null
        }
    }

}