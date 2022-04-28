package com.bookshelfhub.bookshelfhub.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bookshelfhub.bookshelfhub.data.Book
import com.bookshelfhub.bookshelfhub.data.CloudFunctions
import com.bookshelfhub.bookshelfhub.data.models.entities.OrderedBook
import com.bookshelfhub.bookshelfhub.data.repos.cartitems.ICartItemsRepo
import com.bookshelfhub.bookshelfhub.data.repos.orderedbooks.OrderedBooksRepo
import com.bookshelfhub.bookshelfhub.data.repos.paymenttransaction.IPaymentTransactionRepo
import com.bookshelfhub.bookshelfhub.data.sources.remote.RemoteDataFields
import com.bookshelfhub.bookshelfhub.helpers.Json
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.helpers.cloudfunctions.ICloudFunctions
import com.bookshelfhub.bookshelfhub.helpers.cloudfunctions.firebase.FirebaseCloudFunctions
import com.bookshelfhub.bookshelfhub.helpers.notification.ICloudMessaging
import com.bookshelfhub.bookshelfhub.helpers.notification.firebase.CloudMessaging
import com.bookshelfhub.bookshelfhub.helpers.payment.Payment
import com.bookshelfhub.bookshelfhub.helpers.settings.Settings
import com.google.firebase.firestore.FieldValue
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber

@HiltWorker
class UploadPaymentTransactions @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val userAuth: IUserAuth,
    private val cartItemsRepo: ICartItemsRepo,
    private val orderedBooksRepo: OrderedBooksRepo,
    private val paymentTransactionRepo: IPaymentTransactionRepo,
    private val cloudMessaging: ICloudMessaging,
    private val cloudFunctions: ICloudFunctions,
    private val json: Json,
) : CoroutineWorker(
    context,
    workerParams
) {

    override suspend fun doWork(): Result {

        if (!userAuth.getIsUserAuthenticated()) {
            return Result.success()
        }

        val transactionRef = inputData.getString(Payment.TRANSACTION_REF.KEY)!!
        val paymentTransactions = paymentTransactionRepo.getPaymentTransactions(transactionRef)

        if (paymentTransactions.isEmpty()) {
            return Result.success()
        }

      return  try {
            val orderedBooks = emptyList<OrderedBook>()
            val transactionBooksIds = emptyList<String>()
            val totalNoOfOrderedBooks = orderedBooksRepo.getTotalNoOfOrderedBooks()

            for (trans in paymentTransactions) {
                transactionBooksIds.plus(trans.bookId)

                val orderedBook = OrderedBook(
                    trans.bookId, trans.priceInUSD,
                    trans.userId, trans.name,
                    trans.coverUrl, trans.pubId,
                    trans.orderedCountryCode,
                    transactionRef, null,
                    null, 0, 0,
                    totalNoOfOrderedBooks.toLong(), trans.additionInfo
                )
                orderedBooks.plus(orderedBook)
                totalNoOfOrderedBooks.plus(1)
            }
            val userNotificationToken = cloudMessaging.getNotificationTokenAsync()

          val jsonOfOrderedBooks = json.getJsonString(orderedBooks)

          val data = hashMapOf(
              Payment.TRANSACTION_REF.KEY to transactionRef,
              RemoteDataFields.NOTIFICATION_TOKEN to userNotificationToken,
              Payment.ORDERED_BOOKS.KEY to jsonOfOrderedBooks
          )

          cloudFunctions.call(functionName = CloudFunctions.chargeCard, data)

            //Run a callable cloud function with payment ref, notification token, and list of ordered books

            cartItemsRepo.deleteFromCart(transactionBooksIds)
            Result.success()
        } catch (e: Exception) {
            Timber.e(e)
            return Result.success()
        } finally {
            paymentTransactionRepo.deletePaymentTransactions(paymentTransactions)
            Result.success()
        }
    }
}