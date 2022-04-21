package com.bookshelfhub.bookshelfhub.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bookshelfhub.bookshelfhub.data.repos.cartitems.ICartItemsRepo
import com.bookshelfhub.bookshelfhub.data.repos.paymenttransaction.IPaymentTransactionRepo
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber

@HiltWorker
class UploadPaymentTransactions @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val userAuth: IUserAuth,
    private val cartItemsRepo: ICartItemsRepo,
    private val paymentTransactionRepo: IPaymentTransactionRepo,
) : CoroutineWorker(
    context,
    workerParams
) {

    override suspend fun doWork(): Result {

        if (!userAuth.getIsUserAuthenticated()) {
            return Result.success()
        }

        val paymentTrans = paymentTransactionRepo.getAllPaymentTransactions()
        val userId = userAuth.getUserId()

        if(paymentTrans.isEmpty()){
            Result.success()
        }

        return try {

            paymentTransactionRepo.uploadPaymentTransaction(paymentTrans, userId)

            val transactionBooksISBNs = mutableListOf<String>()
            for (trans in paymentTrans) {
                transactionBooksISBNs.add(trans.bookId)
            }

            cartItemsRepo.deleteFromCart(transactionBooksISBNs)
            paymentTransactionRepo.deleteAllPaymentTransactions()

            Result.success()
        } catch (e: Exception) {
            Timber.e(e)
            return Result.retry()
        }



    }
}