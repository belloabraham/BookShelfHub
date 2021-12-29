package com.bookshelfhub.bookshelfhub.helpers.currencyconverter

object Converter{

    private const val  andFromEq = "&from="
    private const val andToEq = "&to="
    private const val andAmountEq = "&amount="

    /**
     * &from="USD"&to="NGN"&amount="1000"
     */
    fun getQueryParam(fromCurrency: String, toCurrency: String, amount:Double): String {
        return "$andFromEq+$fromCurrency+$andToEq+$toCurrency$andAmountEq$amount"
    }

    const val CONVERSION_ENDPOINT= "currency_conversion_endpoint"
}