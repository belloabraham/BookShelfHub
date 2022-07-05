package com.bookshelfhub.bookshelfhub.helpers.utils.payment

import com.bookshelfhub.bookshelfhub.R
import org.junit.Assert.assertTrue
import org.junit.Test

class CardUtilTest {


    @Test
    fun shouldReturnVisaCardDrawable(){
         assertTrue(R.drawable.card_visa == CardUtil.getCardDrawableRes("Visa"))
    }

    @Test
    fun shouldReturnMasterCardDrawable(){
        assertTrue(R.drawable.card_master == CardUtil.getCardDrawableRes("MasterCard"))
    }

    @Test
    fun shouldReturnJCBCardDrawable(){
        assertTrue(R.drawable.card_jcb == CardUtil.getCardDrawableRes("JCB"))
    }

    @Test
    fun shouldReturnVerveCardDrawable(){
        assertTrue(R.drawable.card_verve == CardUtil.getCardDrawableRes("VERVE"))
    }

    @Test
    fun shouldReturnDiscoverCardDrawable(){
        assertTrue(R.drawable.card_discover == CardUtil.getCardDrawableRes("Discover"))
    }

    @Test
    fun shouldReturnAmericanExpressCardDrawable(){
        assertTrue(R.drawable.card_american_express == CardUtil.getCardDrawableRes("American Express"))
    }

    @Test
    fun shouldReturnDinersClubCardDrawable(){
        assertTrue(R.drawable.card_diners_clud == CardUtil.getCardDrawableRes("Diners Club"))
    }

    @Test
    fun shouldReturnUnknownCardDrawable(){
        assertTrue(R.drawable.card_others == CardUtil.getCardDrawableRes("Unknown"))
    }

}
