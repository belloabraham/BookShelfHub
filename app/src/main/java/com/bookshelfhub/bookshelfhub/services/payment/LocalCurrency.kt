package com.bookshelfhub.bookshelfhub.services.payment

object LocalCurrency {

    fun get(countryCode:String): String {
        return when (countryCode) {
            Countries.NIGERIA.COUNTRY_CODE -> {
                Currency.NGN.Value
            }
            Countries.GHANA.COUNTRY_CODE -> {
                Currency.GHS.Value
            }
            Countries.SOUTH_AFRICA.COUNTRY_CODE -> {
                Currency.ZAR.Value
            }
            else -> {
                Currency.USD.Value
            }
        }
    }
}