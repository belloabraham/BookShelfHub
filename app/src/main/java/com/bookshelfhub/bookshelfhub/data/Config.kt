package com.bookshelfhub.bookshelfhub.data

import com.bookshelfhub.bookshelfhub.BuildConfig

object Config {
    const val BOOK_REPORT_URL = "book_report_url"
    const val EMAIL="email"
    const val PHONE="phone"

    fun isDevMode(): Boolean{
        return BuildConfig.DEBUG
    }
}