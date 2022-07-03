package com.bookshelfhub.bookshelfhub.helpers.payment


object Currency {
    
    const val USD="USD"
    private const val NGN="NGN"
    private const val ZAR="ZAR"
    private const val GHS="GHS"

    fun getLocalCurrencyOrUSD(countryCode:String): String {
        return when (countryCode) {
            PaystackSupportedCountriesCode.NIGERIA ->
                NGN
            PaystackSupportedCountriesCode.GHANA ->
                GHS
            PaystackSupportedCountriesCode.SOUTH_AFRICA ->
                ZAR
            else ->
                USD
        }
    }
}