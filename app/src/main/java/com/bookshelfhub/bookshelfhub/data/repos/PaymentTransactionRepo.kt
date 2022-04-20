package com.bookshelfhub.bookshelfhub.data.repos

import com.bookshelfhub.bookshelfhub.data.models.entities.PaymentTransaction
import com.bookshelfhub.bookshelfhub.data.repos.sources.local.PaymentTransactionDao
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.IRemoteDataSource
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.RemoteDataFields
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PaymentTransactionRepo @Inject constructor(
    private  val  paymentTransactionDao: PaymentTransactionDao,
    private val remoteDataSource: IRemoteDataSource) {
    
    suspend fun addPaymentTransactions(paymentTransactions: List<PaymentTransaction>) {
        withContext(IO){ paymentTransactionDao.insertAllOrReplace(paymentTransactions)}
    }

    suspend fun uploadPaymentTransaction(paymentTransactions:List<PaymentTransaction>, userId:String): Void? {
       return remoteDataSource.addListOfDataAsync(
            RemoteDataFields.USERS_COLL,
            userId,
            RemoteDataFields.TRANSACTIONS_COLL,
            paymentTransactions
        )
    }

    suspend fun getAllPaymentTransactions(): List<PaymentTransaction> {
        return withContext(IO){ paymentTransactionDao.getAllPaymentTransactions()}
    }

    suspend fun deleteAllPaymentTransactions() {
        withContext(IO){ paymentTransactionDao.deleteAllPaymentTransactions()}
    }
}