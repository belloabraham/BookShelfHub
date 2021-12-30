package com.bookshelfhub.bookshelfhub.helpers.currencyconverter

import com.bookshelfhub.bookshelfhub.services.payment.SupportedCountries


object Currency {
    
    const val USD="USD"
    const val NGN="NGN"
    const val ZAR="ZAR"
    const val GHS="GHS"

    fun getLocalCurrency(countryCode:String): String {
        return when (countryCode) {
            SupportedCountries.NIGERIA.COUNTRY_CODE ->
                NGN
            SupportedCountries.GHANA.COUNTRY_CODE ->
                GHS
            SupportedCountries.SOUTH_AFRICA.COUNTRY_CODE ->
                ZAR
            else ->
                USD
        }
    }
}