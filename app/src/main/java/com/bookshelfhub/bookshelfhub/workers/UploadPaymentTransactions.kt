package com.bookshelfhub.bookshelfhub.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.RemoteDataFields
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.IRemoteDataSource
import com.bookshelfhub.bookshelfhub.helpers.database.ILocalDb
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class UploadPaymentTransactions @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val userAuth: IUserAuth,
    private val localDb: ILocalDb,
    private val remoteDataSource: IRemoteDataSource
) : CoroutineWorker(
    context,
    workerParams
) {

    override suspend fun doWork(): Result {

        if (!userAuth.getIsUserAuthenticated()) {
            return Result.success()
        }

        val paymentTrans = localDb.getAllPaymentTransactions()
        val userId = userAuth.getUserId()

        return if (paymentTrans.isNotEmpty()) {

            try {
                remoteDataSource.addListOfDataAsync(
                    RemoteDataFields.USERS_COLL,
                    userId,
                    RemoteDataFields.TRANSACTIONS_COLL,
                    paymentTrans
                ).await()

                //Get ISBN of all books in transaction
                val transactionBooksISBNs = mutableListOf<String>()
                for (trans in paymentTrans) {
                    transactionBooksISBNs.add(trans.bookId)
                }
                //Delete all book that are in the Payment transaction record form the cart record so user does not other them again and for better UX
                localDb.deleteFromCart(transactionBooksISBNs)
                localDb.deleteAllPaymentTransactions()

                Result.success()
            } catch (e: Exception) {
                return Result.retry()
            }

        } else {
            Result.success()
        }


    }
}