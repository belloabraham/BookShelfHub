package com.bookshelfhub.bookshelfhub.helpers.currencyconverter

import com.bookshelfhub.bookshelfhub.helpers.payment.GPaySupportedCountries
import com.bookshelfhub.bookshelfhub.helpers.payment.SupportedCountries


object Currency {
    
    const val USD="USD"
    private const val NGN="NGN"
    private const val ZAR="ZAR"
    private const val GHS="GHS"

    private const val KES="KES"
    private const val UGX="UGX"
    private const val TZX="TZX"

    fun getLocalCurrency(countryCode:String): String {
        return when (countryCode) {
            SupportedCountries.NIGERIA.COUNTRY_CODE ->
                NGN
            SupportedCountries.GHANA.COUNTRY_CODE ->
                GHS
            SupportedCountries.TANZANIA.COUNTRY_CODE ->
                TZX
            SupportedCountries.UGANDA.COUNTRY_CODE ->
                UGX
            GPaySupportedCountries.SOUTH_AFRICA.COUNTRY_CODE ->
                ZAR
            GPaySupportedCountries.KENYA.COUNTRY_CODE ->
                KES
            else ->
                USD
        }
    }
}