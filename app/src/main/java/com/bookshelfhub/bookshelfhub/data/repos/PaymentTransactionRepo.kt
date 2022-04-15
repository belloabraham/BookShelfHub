package com.bookshelfhub.bookshelfhub.data.repos

import com.bookshelfhub.bookshelfhub.data.models.entities.PaymentTransaction
import com.bookshelfhub.bookshelfhub.data.repos.sources.local.PaymentTransactionDao
import com.bookshelfhub.bookshelfhub.workers.Worker
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PaymentTransactionRepo @Inject constructor(private  val  paymentTransactionDao: PaymentTransactionDao, private val worker: Worker) {
    
    suspend fun addPaymentTransactions(paymentTransactions: List<PaymentTransaction>) {
        withContext(IO){ paymentTransactionDao.addPaymentTransactions(paymentTransactions)}
    }

    suspend fun getAllPaymentTransactions(): List<PaymentTransaction> {
        return withContext(IO){ paymentTransactionDao.getAllPaymentTransactions()}
    }

    suspend fun deleteAllPaymentTransactions() {
        withContext(IO){ paymentTransactionDao.deleteAllPaymentTransactions()}
    }
}