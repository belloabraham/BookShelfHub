package com.bookshelfhub.bookshelfhub.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bookshelfhub.bookshelfhub.services.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.services.database.cloud.DbFields
import com.bookshelfhub.bookshelfhub.services.database.cloud.ICloudDb
import com.bookshelfhub.bookshelfhub.services.database.local.ILocalDb
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class UploadTransactions @AssistedInject constructor (
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val userAuth:IUserAuth, private val localDb:ILocalDb,
    private val cloudDb: ICloudDb
) : CoroutineWorker(context,
    workerParams
) {

    override suspend fun doWork(): Result {

        if (userAuth.getIsUserAuthenticated()){

            val paymentTrans  = localDb.getAllPaymentTransactions()
            val userId = userAuth.getUserId()

           if (paymentTrans.isNotEmpty()) {

            val task =  cloudDb.addListOfDataAsync(DbFields.USERS.KEY, userId, DbFields.TRANSACTIONS.KEY, paymentTrans)

               if (task.isSuccessful){
                       //Get ISBN of all books in transaction
                       val transactionBooksISBNs = emptyList<String>()
                       for (trans in paymentTrans){
                           transactionBooksISBNs.plus(trans.isbn)
                       }
                       //Delete all book that are in the Payment transaction record form the cart record so user does not other them again and for better UX
                       localDb.deleteFromCart(transactionBooksISBNs)
                       localDb.deleteAllPaymentTransactions()
               }else{
                   return Result.retry()
               }
           }
        }

        return Result.success()
    }
}