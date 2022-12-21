package com.bookshelfhub.core.dynamic_link

/**
 * Key to Pass and get Referrers deep link from SplashActivity
 */
object Referrer{
    const val ID = "ref_id"
    const val REF_LINK = "ref_link"
    /**
     * Used to separate referrer id from isbn if referrer is Publisher
     */
    const val SEPARATOR = "@"
}