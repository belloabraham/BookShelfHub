package com.bookshelfhub.bookshelfhub.helpers.webapi.currencyconverter

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
            SupportedCountries.NIGERIA ->
                NGN
            SupportedCountries.GHANA ->
                GHS
            SupportedCountries.TANZANIA ->
                TZX
            SupportedCountries.UGANDA ->
                UGX
            GPaySupportedCountries.SOUTH_AFRICA ->
                ZAR
            GPaySupportedCountries.KENYA ->
                KES
            else ->
                USD
        }
    }
}