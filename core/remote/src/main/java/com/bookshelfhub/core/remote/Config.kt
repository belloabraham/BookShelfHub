package com.bookshelfhub.core.remote

object Config {

    fun isDevMode(): Boolean{
        return BuildConfig.DEBUG
    }
}