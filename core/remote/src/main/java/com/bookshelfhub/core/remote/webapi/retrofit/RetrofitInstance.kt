package com.bookshelfhub.core.remote.webapi.retrofit

import com.bookshelfhub.core.remote.webapi.FixerAPI
import com.bookshelfhub.core.remote.webapi.PerspectiveAPI
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object  RetrofitInstance {

    val fixerConversionAPI : FixerConversionAPI by lazy {
        Retrofit.Builder()
            .baseUrl(FixerAPI.ENDPOINT)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FixerConversionAPI::class.java)
    }

    val perspectiveAPI : PerspectiveWordAnalyzerAPI by lazy {
        Retrofit.Builder()
            .baseUrl(PerspectiveAPI.ENDPOINT)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PerspectiveWordAnalyzerAPI::class.java)
    }
}

