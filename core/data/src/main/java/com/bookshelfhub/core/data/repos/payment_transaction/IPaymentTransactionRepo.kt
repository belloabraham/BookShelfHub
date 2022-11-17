package com.bookshelfhub.core.data.repos.payment_transaction

import com.bookshelfhub.core.model.entities.PaymentTransaction
import com.bookshelfhub.payment.PaymentSDKType


interface IPaymentTransactionRepo {
    suspend fun initializePaymentVerificationProcess(
        paymentTransactions: List<PaymentTransaction>,
        currencyToChargeForBookSale:String,
        paymentSDKType: PaymentSDKType,
        subtractedUserEarnings:Double
    )
    suspend fun deletePaymentTransactions(paymentTransactions:List<PaymentTransaction>)
    suspend fun getAllPaymentTransactions(): List<PaymentTransaction>
    suspend fun getPaymentTransactions(transactionRef:String): List<PaymentTransaction>
}