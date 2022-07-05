package com.bookshelfhub.bookshelfhub.helpers.payment

import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CurrencyTest {

    @Before
    fun setup(){
        //Executes before each test call, meaning if we have 3 test before gets called 3 times, each time before the test                  //Test are called in a random order
    }


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