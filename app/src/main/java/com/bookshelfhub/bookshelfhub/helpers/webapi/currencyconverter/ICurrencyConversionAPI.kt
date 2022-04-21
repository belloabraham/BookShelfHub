package com.bookshelfhub.bookshelfhub.helpers.webapi.currencyconverter

import com.bookshelfhub.bookshelfhub.data.models.apis.convertion.Fixer
import retrofit2.Response

interface ICurrencyConversionAPI {
    suspend fun convert(
        key: String,
        fromCurrency: String,
        toCurrency: String,
        amount: Double
    ): Response<Fixer>
}