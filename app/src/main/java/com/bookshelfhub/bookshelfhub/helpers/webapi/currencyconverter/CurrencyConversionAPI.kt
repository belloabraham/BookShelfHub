package com.bookshelfhub.bookshelfhub.helpers.webapi.currencyconverter

import com.bookshelfhub.bookshelfhub.data.models.apis.convertion.Fixer
import com.bookshelfhub.bookshelfhub.helpers.webapi.retrofit.FixerConversionAPI
import retrofit2.Response

class CurrencyConversionAPI(private val fixerConversionAPI: FixerConversionAPI) :
    ICurrencyConversionAPI {

      override suspend fun convert(key:String, fromCurrency:String, toCurrency:String, amount:Double):Response<Fixer>{
          return fixerConversionAPI.convert(key, fromCurrency, toCurrency, amount)
      }

}