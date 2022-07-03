package com.bookshelfhub.bookshelfhub.helpers.payment

import org.junit.Assert
import org.junit.Test

class PaymentSDKTest {

    @Test
    fun returnPayStackForGhana(){
      Assert.assertTrue(PaymentSDKType.PAYSTACK == PaymentSDK.getType("GH"))
    }

    @Test
    fun returnPayStackForNigeria(){
        Assert.assertTrue(PaymentSDKType.PAYSTACK == PaymentSDK.getType("NG"))
    }

    @Test
    fun returnPayStackForSouthAfrica(){
        Assert.assertTrue(PaymentSDKType.PAYSTACK == PaymentSDK.getType("SA"))
    }

}