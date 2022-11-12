package com.bookshelfhub.core.remote.webapi.currencyconverter

import com.bookshelfhub.core.model.apis.convertion.Fixer
import com.bookshelfhub.core.remote.webapi.retrofit.FixerConversionAPI
import retrofit2.Response

class CurrencyConversionAPI(private val fixerConversionAPI: FixerConversionAPI) :
    ICurrencyConversionAPI {

      override suspend fun convert(key:String, fromCurrency:String, toCurrency:String, amount:Double):Response<Fixer>{
          return fixerConversionAPI.convert(key, fromCurrency, toCurrency, amount)
      }

}