package com.bookshelfhub.bookshelfhub.data.repos.paymenttransaction

import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import com.bookshelfhub.bookshelfhub.data.models.entities.PaymentTransaction
import com.bookshelfhub.bookshelfhub.data.sources.local.PaymentTransactionDao
import com.bookshelfhub.bookshelfhub.data.sources.local.RoomInstance
import com.bookshelfhub.bookshelfhub.data.sources.remote.IRemoteDataSource
import com.bookshelfhub.bookshelfhub.data.sources.remote.RemoteDataFields
import com.bookshelfhub.bookshelfhub.workers.Constraint
import com.bookshelfhub.bookshelfhub.workers.UploadPaymentTransactions
import com.bookshelfhub.bookshelfhub.workers.Worker
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PaymentTransactionRepo @Inject constructor(
    roomInstance: RoomInstance,
    private val worker: Worker,
    private val remoteDataSource: IRemoteDataSource,
    ) : IPaymentTransactionRepo {

    private  val  paymentTransactionDao = roomInstance.paymentTransDao()
    private val ioDispatcher: CoroutineDispatcher = IO

    override suspend fun addPaymentTransactions(paymentTransactions: List<PaymentTransaction>) {
        withContext(ioDispatcher){ paymentTransactionDao.insertAllOrReplace(paymentTransactions)}
        val oneTimeVerifyPaymentTrans =
            OneTimeWorkRequestBuilder<UploadPaymentTransactions>()
                .setConstraints(Constraint.getConnected())
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()
        worker.enqueue(oneTimeVerifyPaymentTrans)
    }

    override suspend fun uploadPaymentTransaction(paymentTransactions:List<PaymentTransaction>, userId:String): Void? {
       return remoteDataSource.addListOfDataAsync(
            RemoteDataFields.USERS_COLL,
            userId,
            RemoteDataFields.TRANSACTIONS_COLL,
            paymentTransactions
        )
    }

    override suspend fun getAllPaymentTransactions(): List<PaymentTransaction> {
        return withContext(ioDispatcher){ paymentTransactionDao.getAllPaymentTransactions()}
    }

    override suspend fun deleteAllPaymentTransactions() {
        withContext(ioDispatcher){ paymentTransactionDao.deleteAllPaymentTransactions()}
    }
}