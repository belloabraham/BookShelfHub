package com.bookshelfhub.bookshelfhub.workers

import android.content.Context
import androidx.annotation.StringRes
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.data.Book
import com.bookshelfhub.bookshelfhub.helpers.cloudfunctions.CloudFunctions
import com.bookshelfhub.bookshelfhub.data.models.entities.OrderedBook
import com.bookshelfhub.bookshelfhub.data.repos.cartitems.ICartItemsRepo
import com.bookshelfhub.bookshelfhub.data.repos.orderedbooks.OrderedBooksRepo
import com.bookshelfhub.bookshelfhub.data.repos.paymenttransaction.IPaymentTransactionRepo
import com.bookshelfhub.bookshelfhub.data.repos.referral.ReferralRepo
import com.bookshelfhub.bookshelfhub.data.repos.user.UserRepo
import com.bookshelfhub.bookshelfhub.data.sources.remote.RemoteDataFields
import com.bookshelfhub.bookshelfhub.helpers.Json
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.helpers.cloudfunctions.ICloudFunctions
import com.bookshelfhub.bookshelfhub.helpers.notification.ICloudMessaging
import com.bookshelfhub.bookshelfhub.helpers.notification.NotificationBuilder
import com.bookshelfhub.bookshelfhub.helpers.payment.Payment
import com.bookshelfhub.bookshelfhub.helpers.settings.SettingsUtil
import com.google.firebase.functions.FirebaseFunctionsException
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
            var orderedBooks = emptyList<OrderedBook>()
            var transactionBooksIds = emptyList<String>()
            var bookNames = ""
            var totalNoOfOrderedBooksAsSerialNo = orderedBooksRepo.getTotalNoOfOrderedBooks().toLong()
            val isFirstPurchase = totalNoOfOrderedBooksAsSerialNo <=0
            var totalAmountInUSD = 0.0
            for (trans in paymentTransactions) {
                var collabCommissionInPercentage:Double? = null
                var collabId:String? = null
                val optionalCollab = referralRepo.getAnOptionalCollaborator(trans.bookId)
                if(optionalCollab.isPresent){
                  val  collab = optionalCollab.get()
                    collabId = collab.collabId
                    collabCommissionInPercentage = collab.commissionInPercentage
                }

                totalAmountInUSD = totalAmountInUSD.plus(trans.priceInUSD)
                transactionBooksIds = transactionBooksIds.plus(trans.bookId)
                val orderedBook = OrderedBook(
                    trans.bookId, trans.priceInUSD,
                    trans.userId, trans.name,
                    trans.coverUrl, trans.pubId,
                    trans.orderedCountryCode,
                    transactionRef, null,  0, 0,
                    totalNoOfOrderedBooksAsSerialNo, trans.additionInfo,
                    collabCommissionInPercentage,
                    collabId,
                    trans.priceInBookCurrency
                )
               bookNames =  bookNames.plus("${trans.name}, ")
                orderedBooks = orderedBooks.plus(orderedBook)
                totalNoOfOrderedBooksAsSerialNo = totalNoOfOrderedBooksAsSerialNo.plus(1)
            }

          val userNotificationToken = cloudMessaging.getNotificationTokenAsync()
          val jsonStringOfOrderedBooks = json.getJsonString(orderedBooks)


          var userReferralId:String? = null
          var userReferrerCommission = 0.0
          if(isFirstPurchase){
              val userId = userAuth.getUserId()
              userReferralId = userRepo.getUser(userId).get().referrerId
              userReferrerCommission = paymentTransactions[0].priceInUSD * 0.1
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

          settingsUtil.setBoolean(Book.IS_NEWLY_PURCHASED, true)

          Result.success()

        } catch (e: Exception) {
            val errorIsNotCloudFunctionException = e !is FirebaseFunctionsException
            if(errorIsNotCloudFunctionException){
                val message = String.format(applicationContext.getString(R.string.unable_to_process_payment), e.message)
                showNotification(message, R.string.payment_trans_failed)
            }
            Timber.e(e)
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