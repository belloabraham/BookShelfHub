package com.bookshelfhub.book.purchase.workers

import android.content.Context
import androidx.annotation.StringRes
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bookshelfhub.book.purchase.R
import com.bookshelfhub.core.authentication.IUserAuth
import com.bookshelfhub.core.cloud.messaging.ICloudMessaging
import com.bookshelfhub.core.common.helpers.ErrorUtil
import com.bookshelfhub.core.common.helpers.Json
import com.bookshelfhub.core.common.notification.NotificationBuilder
import com.bookshelfhub.core.data.repos.cartitems.ICartItemsRepo
import com.bookshelfhub.core.data.repos.ordered_books.OrderedBooksRepo
import com.bookshelfhub.core.data.repos.payment_transaction.IPaymentTransactionRepo
import com.bookshelfhub.core.data.repos.referral.ReferralRepo
import com.bookshelfhub.core.data.repos.user.UserRepo
import com.bookshelfhub.core.datastore.settings.SettingsUtil
import com.bookshelfhub.core.model.entities.OrderedBook
import com.bookshelfhub.core.remote.cloud_functions.CloudFunctions
import com.bookshelfhub.core.remote.cloud_functions.ICloudFunctions
import com.bookshelfhub.core.remote.database.RemoteDataFields
import com.bookshelfhub.payment.Payment
import com.google.firebase.functions.FirebaseFunctionsException
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

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
    private val userRepo: UserRepo,
    private val referralRepo: ReferralRepo,
    private val json: Json,
    private val settingsUtil: SettingsUtil,
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
            val orderedBooks = mutableListOf<OrderedBook>()
            val transactionBooksIds = mutableListOf<String>()
            var bookNames = ""
            var totalNoOfOrderedBooksAsSerialNo = orderedBooksRepo.getTotalNoOfOrderedBooks().toLong()
            val isFirstPurchase = totalNoOfOrderedBooksAsSerialNo <=0
            for (transaction in paymentTransactions) {
                var collabCommissionInPercentage:Double? = null
                var collabId:String? = null
                val optionalCollab = referralRepo.getAnOptionalCollaborator(transaction.bookId)
                if(optionalCollab.isPresent){
                  val  collab = optionalCollab.get()
                    collabId = collab.collabId
                    collabCommissionInPercentage = collab.commissionInPercentage
                }

                transactionBooksIds.add(transaction.bookId)
                val orderedBook = OrderedBook(
                    transaction.bookId, transaction.priceInUSD,
                    transaction.userId, transaction.name,
                    transaction.coverDataUrl, transaction.pubId,
                    transaction.orderedCountryCode,
                    transactionRef, null,  0, 0,
                    totalNoOfOrderedBooksAsSerialNo, transaction.additionInfo,
                    collabCommissionInPercentage,
                    collabId,
                    transaction.priceInBookCurrency
                )
                bookNames =  bookNames.plus("${transaction.name}, ")
                orderedBooks.add(orderedBook)
                totalNoOfOrderedBooksAsSerialNo = totalNoOfOrderedBooksAsSerialNo.plus(1)
            }

          val userNotificationToken = cloudMessaging.getNotificationTokenAsync()
          val jsonStringOfOrderedBooks = json.getJsonString(orderedBooks)


          var userReferralId:String? = null
          var userReferrerCommission = 0.0
          if(isFirstPurchase){
              val userId = userAuth.getUserId()
              userReferralId = userRepo.getUser(userId).get().referrerId
              userReferrerCommission = paymentTransactions[0].priceInBookCurrency * 0.1
          }

          val data = hashMapOf<String, Any?>(
              Payment.TRANSACTION_REF.KEY to transactionRef,
              RemoteDataFields.NOTIFICATION_TOKEN to userNotificationToken,
              Payment.ORDERED_BOOKS.KEY to jsonStringOfOrderedBooks,
              Payment.BOOK_NAMES.KEY to bookNames,
              Payment.USER_REFERRER_ID.KEY to userReferralId,
              Payment.USER_REFERRER_COMMISSION.KEY to userReferrerCommission,
      )

          cloudFunctions.call(functionName = CloudFunctions.completePayStackPaymentTransaction, data)

          cartItemsRepo.deleteFromCart(transactionBooksIds)

          settingsUtil.setBoolean(com.bookshelfhub.core.data.Book.IS_NEWLY_PURCHASED, true)

          Result.success()

        } catch (e: Exception) {
            val errorIsNotCloudFunctionException = e !is FirebaseFunctionsException
            if(errorIsNotCloudFunctionException){
                val message = String.format(applicationContext.getString(R.string.unable_to_process_payment), e.message)
                showNotification(message, R.string.payment_trans_failed)
            }
            ErrorUtil.e(e)
            return Result.success()
        } finally {
            paymentTransactionRepo.deletePaymentTransactions(paymentTransactions)
            Result.success()
        }
    }

    private fun showNotification(message:String, @StringRes title:Int){
        val notificationId = (2..20).random()
        NotificationBuilder(applicationContext)
            .setMessage(message)
            .setTitle(title)
            .Builder(applicationContext)
            .showNotification(notificationId)
    }
}