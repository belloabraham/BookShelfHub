package com.bookshelfhub.payment

object SupportedCountries {

    //Pay Stack
    const val NIGERIA = "NG"
    const val GHANA = "GH"

    //General
    private const val UNITED_STATES = "US"

    fun getAll():List<String>{
        return listOf(
            GHANA,
            NIGERIA,
            UNITED_STATES
        )
    }
}