package com.bookshelfhub.bookshelfhub.helpers.payment

import org.junit.Assert
import org.junit.Test

class CurrencyTest {


    @Test
    fun shouldReturnNGN_For_NG(){
       Assert.assertTrue("NGN" == Currency.getLocalCurrencyOrUSD("NG"))
    }

    @Test
    fun shouldReturnGHS_For_GH(){
        Assert.assertTrue("GHS" == Currency.getLocalCurrencyOrUSD("GH"))
    }

    @Test
    fun shouldReturnZAR_For_SA(){
        Assert.assertTrue("ZAR" == Currency.getLocalCurrencyOrUSD("SA"))
    }

}