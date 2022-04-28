package com.bookshelfhub.bookshelfhub.data.repos.paymenttransaction

import com.bookshelfhub.bookshelfhub.data.models.entities.PaymentTransaction

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