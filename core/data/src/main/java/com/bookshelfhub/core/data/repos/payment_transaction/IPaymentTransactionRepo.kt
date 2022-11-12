package com.bookshelfhub.core.data.repos.payment_transaction

import com.bookshelfhub.core.model.entities.PaymentTransaction


interface IPaymentTransactionRepo {
    suspend fun addPaymentTransactions(paymentTransactions: List<PaymentTransaction>)

    suspend fun uploadPaymentTransaction(
        paymentTransactions: List<PaymentTransaction>,
        userId: String
    ): Void?

    suspend fun deletePaymentTransactions(paymentTransactions:List<PaymentTransaction>)
    suspend fun getAllPaymentTransactions(): List<PaymentTransaction>
    suspend fun getPaymentTransactions(transactionRef:String): List<PaymentTransaction>
    suspend fun deleteAllPaymentTransactions()
}