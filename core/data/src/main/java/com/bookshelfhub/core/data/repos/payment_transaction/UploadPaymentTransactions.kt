package com.bookshelfhub.core.data.repos.payment_transaction

import android.content.Context
import androidx.annotation.StringRes
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bookshelfhub.core.authentication.IUserAuth
import com.bookshelfhub.core.cloud.messaging.ICloudMessaging
import com.bookshelfhub.core.common.helpers.ErrorUtil
import com.bookshelfhub.core.common.helpers.Json
import com.bookshelfhub.core.common.notification.NotificationBuilder
import com.bookshelfhub.core.data.Book
import com.bookshelfhub.core.data.repos.cartitems.ICartItemsRepo
import com.bookshelfhub.core.data.repos.ordered_books.OrderedBooksRepo
import com.bookshelfhub.core.data.repos.referral.ReferralRepo
import com.bookshelfhub.core.data.repos.user.UserRepo
import com.bookshelfhub.core.datastore.settings.SettingsUtil
import com.bookshelfhub.core.model.entities.OrderedBook
import com.bookshelfhub.core.remote.cloud_functions.CloudFunctions
import com.bookshelfhub.core.remote.cloud_functions.ICloudFunctions
import com.bookshelfhub.core.remote.database.RemoteDataFields
import com.bookshelfhub.payment.Payment
import com.bookshelfhub.core.resources.R
import com.bookshelfhub.payment.PaymentSDKType
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
        val paymentSDKType = inputData.getString(Payment.PAYMENT_SDK_TYPE.KEY)!!
        val currencyToChargeForBookSale = inputData.getString(Payment.CURRENCY_TO_CHARGE_FOR_BOOK_SALE.KEY)!!

        val paymentTransactions = paymentTransactionRepo.getPaymentTransactions(transactionRef)

        if (paymentTransactions.isEmpty()) {
            return Result.success()
        }

      return  try {
            val listOfOrderedBooksInCart = mutableListOf<OrderedBook>()
            val listOfBookIdsToBeDeletedFromCart = mutableListOf<String>()

            val combinedBookNames = StringBuilder()
            var totalNoOfPreviouslyOrderedBooks = orderedBooksRepo.getTotalNoOfOrderedBooks().toLong()
            val isFirstPurchase = totalNoOfPreviouslyOrderedBooks <= 0

            for (transaction in paymentTransactions) {

                var commissionInPercentageAssignedToCollaborator:Double? = null
                var idForCollaborator:String? = null
                val collaboratorForEachBook = referralRepo.getAnOptionalCollaborator(transaction.bookId)

                if(collaboratorForEachBook.isPresent){
                  val collab = collaboratorForEachBook.get()
                    idForCollaborator = collab.collabId
                    commissionInPercentageAssignedToCollaborator = collab.commissionInPercentage
                }

                listOfBookIdsToBeDeletedFromCart.add(transaction.bookId)

                val orderedBook = OrderedBook(
                    transaction.bookId,
                    transaction.userId,
                    transaction.name,
                    transaction.coverDataUrl,
                    transaction.pubId,
                    transaction.sellerCurrency,
                    transaction.userCountryCode,
                    transactionRef,
                    null,
                    ++totalNoOfPreviouslyOrderedBooks,
                    transaction.additionInfo,
                    commissionInPercentageAssignedToCollaborator,
                    idForCollaborator,
                    transaction.price
                )

                combinedBookNames.append("${transaction.name}, ")
                listOfOrderedBooksInCart.add(orderedBook)
            }

          val userNotificationToken = cloudMessaging.getNotificationTokenAsync()
          val listOfOrderedBooksAsJsonString = json.getJsonString(listOfOrderedBooksInCart)


          val userId = userAuth.getUserId()
          val user = userRepo.getUser(userId).get()
          val referrerIsQualifiedForEarnings = isFirstPurchase && user.earningsCurrency == currencyToChargeForBookSale

          var userReferrerCommission = 0.0

          if(referrerIsQualifiedForEarnings && user.referrerId != null){
              userReferrerCommission = paymentTransactions[0].price * 0.1 //10 percent
          }

          val data = hashMapOf<String, Any?>(
              Payment.TRANSACTION_REF.KEY to transactionRef,
              RemoteDataFields.NOTIFICATION_TOKEN to userNotificationToken,
              Payment.ORDERED_BOOKS.KEY to listOfOrderedBooksAsJsonString,
              Payment.BOOK_NAMES.KEY to combinedBookNames.toString(),
              Payment.USER_REFERRER_ID.KEY to user.referrerId,
              Payment.USER_REFERRER_COMMISSION.KEY to userReferrerCommission
          )

          val paymentCloudFunction = getPaymentCLoudFunctionToBeCalled(paymentSDKType)!!
          cloudFunctions.call(functionName = paymentCloudFunction, data)

          cartItemsRepo.deleteFromCart(listOfBookIdsToBeDeletedFromCart)

          //Required by bookshelf tab to know if user just purchase new books
          settingsUtil.setBoolean(Book.IS_NEWLY_PURCHASED, true)

          Result.success()

        } catch (e: Exception) {
            val message = String.format(applicationContext.getString(R.string.unable_to_process_payment), e.message)
            showNotification(message, R.string.payment_trans_failed)
            ErrorUtil.e(e)
            return Result.success()
        } finally {
            paymentTransactionRepo.deletePaymentTransactions(paymentTransactions)
            Result.success()
        }
    }

    private  fun getPaymentCLoudFunctionToBeCalled(paymentSDK:String): String? {
        if(paymentSDK == PaymentSDKType.PAYSTACK.KEY){
           return CloudFunctions.COMPLETE_PAYSTACK_PAYMENT_TRANSACTION_FUNCTION
        }
        return null
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