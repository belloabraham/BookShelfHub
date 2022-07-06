package com.bookshelfhub.bookshelfhub.helpers.payment

import org.junit.Assert.assertTrue
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PaymentSDKTest {

    @Test
    fun returnPayStackForGhana(){
        assertTrue(PaymentSDKType.PAYSTACK == PaymentSDK.getType("GH"))
    }

    @Test
    fun returnPayStackForNigeria(){
        assertTrue(PaymentSDKType.PAYSTACK == PaymentSDK.getType("NG"))
    }

    @Test
    fun returnPayStackForSouthAfrica(){
        assertTrue(PaymentSDKType.PAYSTACK == PaymentSDK.getType("SA"))
    }

}