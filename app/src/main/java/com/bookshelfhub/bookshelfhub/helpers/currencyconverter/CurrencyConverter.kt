package com.bookshelfhub.bookshelfhub.helpers.currencyconverter

object CurrencyConverter{

    private const val  andFromEq = "&from="
    private const val andToEq = "&to="
    private const val andAmountEq = "&amount="

    /**
     * e.g &from="USD"&to="NGN"&amount="1000"
     */
    fun getQueryParam(fromCurrency: String, toCurrency: String, amount:Double): String {
        return "$andFromEq+$fromCurrency+$andToEq+$toCurrency$andAmountEq$amount"
    }

}