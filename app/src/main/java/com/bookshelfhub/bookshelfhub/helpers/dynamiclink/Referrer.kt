package com.bookshelfhub.bookshelfhub.helpers.dynamiclink

/**
 * Key to Pass and get Referrers deep link from SplashActivity
 */
enum class Referrer(val KEY:String) {
    ID("ref_id"),
    BOOK_REFERRED("isbn"),
    REF_LINK("ref_link"),
    /**
     * Used to separate referrer id from isbn if referrer is Publisher
     */
    SEPARATOR("@")
}