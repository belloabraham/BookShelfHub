package com.bookshelfhub.bookshelfhub.helpers.webapi

import com.bookshelfhub.bookshelfhub.data.models.apis.convertion.Fixer
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import retrofit2.Response

class CurrencyConversionAPI(private val fixerConversionAPI: FixerConversionAPI) {

      suspend fun convert(key:String, fromCurrency:String, toCurrency:String, amount:Double):Response<Fixer>{
          return fixerConversionAPI.convert(key, fromCurrency, toCurrency, amount)
      }

}