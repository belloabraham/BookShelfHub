package com.bookshelfhub.payment

object Config {

    fun isDevMode(): Boolean{
        return com.bookshelfhub.core.payment.BuildConfig.DEBUG
    }
}