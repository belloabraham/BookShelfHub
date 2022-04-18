package com.bookshelfhub.bookshelfhub.helpers.webapi

import com.bookshelfhub.bookshelfhub.data.FixerAPI
import com.bookshelfhub.bookshelfhub.data.models.apis.convertion.Fixer
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface FixerConversionAPI {

    @GET(FixerAPI.CONVERSION_PATH)
   suspend fun convert(
        @Query(FixerAPI.ACCESS_KEY_PARAM)accessKey:String,
        @Query(FixerAPI.FROM_PARAM)fromCurrencyCode:String,
        @Query(FixerAPI.TO_PARAM)toCurrencyCode:String,
        @Query(FixerAPI.AMOUNT_PARAM)amount:Double):Response<Fixer>
}