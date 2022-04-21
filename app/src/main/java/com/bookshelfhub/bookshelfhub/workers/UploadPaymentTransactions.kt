package com.bookshelfhub.bookshelfhub.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bookshelfhub.bookshelfhub.data.repos.CartItemsRepo
import com.bookshelfhub.bookshelfhub.data.repos.PaymentTransactionRepo
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.RemoteDataFields
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber

@HiltWorker
class UploadPaymentTransactions @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val userAuth: IUserAuth,
    private val cartItemsRepo: CartItemsRepo,
    private val paymentTransactionRepo: PaymentTransactionRepo,
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