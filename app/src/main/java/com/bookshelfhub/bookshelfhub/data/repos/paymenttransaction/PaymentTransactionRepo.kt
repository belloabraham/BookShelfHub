package com.bookshelfhub.bookshelfhub.data.repos.paymenttransaction

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.workDataOf
import com.bookshelfhub.bookshelfhub.data.models.entities.PaymentTransaction
import com.bookshelfhub.bookshelfhub.data.sources.local.AppDatabase
import com.bookshelfhub.bookshelfhub.data.sources.remote.IRemoteDataSource
import com.bookshelfhub.bookshelfhub.data.sources.remote.RemoteDataFields
import com.bookshelfhub.bookshelfhub.helpers.payment.Payment
import com.bookshelfhub.bookshelfhub.workers.Constraint
import com.bookshelfhub.bookshelfhub.workers.UploadPaymentTransactions
import com.bookshelfhub.bookshelfhub.workers.Worker
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PaymentTransactionRepo @Inject constructor(
    appDatabase: AppDatabase,
    private val worker: Worker,
    private val remoteDataSource: IRemoteDataSource,
    ) : IPaymentTransactionRepo {

    private  val  paymentTransactionDao = appDatabase.getPaymentTransDao()
    private val ioDispatcher: CoroutineDispatcher = IO

    override suspend fun addPaymentTransactions(paymentTransactions: List<PaymentTransaction>) {
        withContext(ioDispatcher){ paymentTransactionDao.insertAllOrReplace(paymentTransactions)}
        val transactionRef = paymentTransactions[0].transactionReference!!

        val workData = workDataOf(
            Payment.TRANSACTION_REF.KEY to transactionRef
        )

        val oneTimeVerifyPaymentTrans =
            OneTimeWorkRequestBuilder<UploadPaymentTransactions>()
                .setConstraints(Constraint.getConnected())
                .setInputData(workData)
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()
        worker.enqueueUniqueWork(tag = transactionRef, ExistingWorkPolicy.KEEP,  oneTimeVerifyPaymentTrans)
    }

    override suspend fun uploadPaymentTransaction(paymentTransactions:List<PaymentTransaction>, userId:String): Void? {
       return remoteDataSource.addListOfDataAsync(
            RemoteDataFields.USERS_COLL,
            userId,
            RemoteDataFields.TRANSACTIONS_COLL,
            paymentTransactions
        )
    }

    override suspend fun getPaymentTransactions(transactionRef:String): List<PaymentTransaction> {
        return withContext(ioDispatcher){
            paymentTransactionDao.getPaymentTransactions(transactionRef)
        }
    }

    override suspend fun deletePaymentTransactions(paymentTransactions:List<PaymentTransaction>){
        withContext(ioDispatcher){
            paymentTransactionDao.deleteAll(paymentTransactions)
        }
    }

    override suspend fun getAllPaymentTransactions(): List<PaymentTransaction> {
        return withContext(ioDispatcher){ paymentTransactionDao.getAllPaymentTransactions()}
    }

    override suspend fun deleteAllPaymentTransactions() {
        withContext(ioDispatcher){ paymentTransactionDao.deleteAllPaymentTransactions()}
    }
}