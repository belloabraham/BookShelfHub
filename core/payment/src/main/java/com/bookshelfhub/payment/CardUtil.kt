package com.bookshelfhub.payment

import co.paystack.android.model.Card
import com.bookshelfhub.core.model.entities.PaymentCard
import com.bookshelfhub.core.resources.R

class CardUtil(paymentCard: PaymentCard) {

    var card = Card(paymentCard.cardNo, paymentCard.expiryMonth, paymentCard.expiryYear, paymentCard.cvv)

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
        fun getCardDrawableRes(cardType:String): Int {
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