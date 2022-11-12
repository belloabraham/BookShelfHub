package com.bookshelfhub.core.remote.webapi.currencyconverter

import com.bookshelfhub.core.model.apis.convertion.Fixer
import retrofit2.Response

interface ICurrencyConversionAPI {
    suspend fun convert(
        key: String,
        fromCurrency: String,
        toCurrency: String,
        amount: Double
    ): Response<Fixer>
}