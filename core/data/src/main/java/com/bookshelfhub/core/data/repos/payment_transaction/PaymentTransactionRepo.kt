package com.bookshelfhub.core.data.repos.payment_transaction

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.workDataOf
import com.bookshelfhub.core.common.worker.Constraint
import com.bookshelfhub.core.database.AppDatabase
import com.bookshelfhub.core.model.entities.PaymentTransaction
import com.bookshelfhub.core.common.worker.Worker
import com.bookshelfhub.payment.Payment
import com.bookshelfhub.payment.PaymentSDKType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PaymentTransactionRepo @Inject constructor(
    appDatabase: AppDatabase,
    private val worker: Worker,
    ) : IPaymentTransactionRepo {

    private  val  paymentTransactionDao = appDatabase.getPaymentTransDao()
    private val ioDispatcher: CoroutineDispatcher = IO

    override suspend fun initializePaymentVerificationProcess(
        paymentTransactions: List<PaymentTransaction>,
        currencyToChargeForBookSale:String,
        paymentSDKType: PaymentSDKType,
        subtractedUserEarnings:Double
    ) {

        withContext(ioDispatcher){
            paymentTransactionDao.insertAllOrReplace(paymentTransactions)
        }

        val transactionRef = paymentTransactions[0].transactionReference!!

        val workData = workDataOf(
            Payment.TRANSACTION_REF.KEY to transactionRef,
            Payment.PAYMENT_SDK_TYPE.KEY to paymentSDKType.KEY,
            Payment.CURRENCY_TO_CHARGE_FOR_BOOK_SALE.KEY to currencyToChargeForBookSale,
            Payment.SUBTRACTED_USER_EARNINGS.KEY to subtractedUserEarnings
        )

        val oneTimeVerifyPaymentTrans =
            OneTimeWorkRequestBuilder<UploadPaymentTransactions>()
                .setConstraints(Constraint.getConnected())
                .setInputData(workData)
                .build()
        worker.enqueueUniqueWork(tag = transactionRef, ExistingWorkPolicy.KEEP,  oneTimeVerifyPaymentTrans)
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
        return withContext(ioDispatcher){
            paymentTransactionDao.getAllPaymentTransactions()
        }
    }

}