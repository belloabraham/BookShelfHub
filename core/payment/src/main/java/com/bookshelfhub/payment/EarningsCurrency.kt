package com.bookshelfhub.payment

object EarningsCurrency {

    fun getByTimeZone(timeZone:String): String {
        if(timeZone.contains("Africa", true)){
            return SupportedCurrencies.NGN
        }
        return SupportedCurrencies.USD
    }
}