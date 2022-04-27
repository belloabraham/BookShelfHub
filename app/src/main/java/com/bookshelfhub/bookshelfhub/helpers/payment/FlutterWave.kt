package com.bookshelfhub.bookshelfhub.helpers.payment

import com.bookshelfhub.bookshelfhub.data.Config
import com.bookshelfhub.bookshelfhub.data.models.entities.PaymentCard
import com.flutterwave.raveandroid.rave_java_commons.Meta
import com.flutterwave.raveandroid.rave_presentation.RaveNonUIManager
import com.flutterwave.raveandroid.rave_presentation.card.Card
import com.flutterwave.raveandroid.rave_presentation.card.CardPaymentCallback
import com.flutterwave.raveandroid.rave_presentation.card.CardPaymentManager

class FlutterWave(private val encryptionKey:String,
                  private val deviceBrandAndModel:String,
                  private val userFirstname:String,
                  private val userLastName:String,
                  private val email:String,
                  private val bookIds:String,
                  private val metaList:List<Meta>,
                  private val paymentCallBack:CardPaymentCallback,
                  private val isDebugMode:Boolean = Config.isDevMode()
):IPayment {

    override fun chargeCard(publicKey:String, paymentCard: PaymentCard, amount:Double, currency:String){

        val raveNonUIManager = RaveNonUIManager()
            .setCurrency(currency)
            .setAmount(amount)
            .setEmail(email)
            .setfName(userFirstname)
            .setlName(userLastName)
            .setPublicKey(publicKey)
            .setEncryptionKey(encryptionKey)
            .setTxRef(bookIds)
            .setMeta(metaList)
            .onStagingEnv(isDebugMode)
            .setUniqueDeviceId(deviceBrandAndModel)
            .initialize()

        val raveCard = Card(paymentCard.cardNo, "${paymentCard.expiryMonth}", "${paymentCard.expiryYear}", paymentCard.cvv)
        val cardPayManager = CardPaymentManager(raveNonUIManager, paymentCallBack )
        cardPayManager.chargeCard(raveCard)
    }

}