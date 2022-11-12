package com.bookshelfhub.core.remote.webapi.retrofit

import com.bookshelfhub.core.model.apis.convertion.Fixer
import com.bookshelfhub.core.remote.webapi.FixerAPI
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