package com.bookshelfhub.bookshelfhub

object Config {
    fun isDevMode(): Boolean{
        return BuildConfig.DEBUG
    }
}