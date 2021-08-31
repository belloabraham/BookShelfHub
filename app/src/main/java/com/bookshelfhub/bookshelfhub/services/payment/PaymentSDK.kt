package com.bookshelfhub.bookshelfhub.services.payment

object PaymentSDK {

    fun get(countryCode:String?): SDKType? {
        return if(countryCode == Countries.NIGERIA.COUNTRY_CODE || countryCode == Countries.GHANA.COUNTRY_CODE || countryCode == Countries.SOUTH_AFRICA.COUNTRY_CODE ){
            SDKType.PAYSTACK
        }else{
            null
        }
    }

}