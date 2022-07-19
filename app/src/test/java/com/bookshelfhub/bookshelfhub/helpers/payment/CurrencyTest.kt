package com.bookshelfhub.bookshelfhub.helpers.payment

import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CurrencyTest {

    @Test
    fun shouldReturnNGN_For_NG(){
       assertTrue("NGN" == Currency.getLocalCurrencyOrUSD("NG"))
    }

    @Test
    fun shouldReturnGHS_For_GH(){
        assertTrue("GHS" == Currency.getLocalCurrencyOrUSD("GH"))
    }

    @Test
    fun shouldReturnZAR_For_SA(){
        assertTrue("ZAR" == Currency.getLocalCurrencyOrUSD("SA"))
    }

}