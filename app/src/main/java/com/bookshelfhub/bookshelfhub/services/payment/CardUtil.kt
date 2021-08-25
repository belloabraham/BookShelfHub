package com.bookshelfhub.bookshelfhub.services.payment

import android.app.Activity
import co.paystack.android.Paystack
import co.paystack.android.PaystackSdk
import co.paystack.android.model.Card
import co.paystack.android.model.Charge
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.PaymentCard

class CardUtil(paymentCard: PaymentCard) {

     var card:Card = Card(paymentCard.cardNo, paymentCard.expiryMonth, paymentCard.expiryYear, paymentCard.cvv)

    fun isValidCard(paymentCard: PaymentCard): Boolean {
       return Card(paymentCard.cardNo, paymentCard.expiryMonth, paymentCard.expiryYear, paymentCard.cvv).validCVC()
    }

    fun isValidCVC(): Boolean {
        return card.validCVC()
    }

    fun getLastForDigit(): String {
        return card.last4digits
    }

    fun getCardType(): String {
        return card.type
    }


    fun isValidExpDate(): Boolean {
        return card.validExpiryDate()
    }

    fun isValidCardNo(): Boolean {
        return card.validNumber()
    }


    companion object{
        fun getDrawableRes(cardType:String): Int {
            return when (cardType) {
                Card.CardType.VISA -> {
                    R.drawable.card_visa
                }
                Card.CardType.MASTERCARD -> {
                    R.drawable.card_master
                }
                Card.CardType.VERVE -> {
                    R.drawable.card_verve
                }
                Card.CardType.AMERICAN_EXPRESS -> {
                    R.drawable.card_american_express
                }
                Card.CardType.JCB -> {
                    R.drawable.card_jcb
                }
                Card.CardType.DINERS_CLUB -> {
                    R.drawable.card_diners_clud
                }
                Card.CardType.DISCOVER -> {
                    R.drawable.card_discover
                }else -> {
                    R.drawable.card_others
                }
            }
        }
    }


}