package com.bookshelfhub.feature.book_reviews.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bookshelfhub.core.authentication.IUserAuth
import com.bookshelfhub.core.common.extensions.containsUrl
import com.bookshelfhub.core.common.helpers.ErrorUtil
import com.bookshelfhub.core.data.repos.ordered_books.IOrderedBooksRepo
import com.bookshelfhub.core.data.repos.user_review.IUserReviewRepo
import com.bookshelfhub.core.remote.database.RemoteDataFields
import com.google.firebase.firestore.FieldValue
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/*
* This worker is run periodically to check if the user has bought the book for which it previously reviewed.
* If so update the user review that was previously posted to the cloud as unverified to verified
* */

@HiltWorker
class UpdatePreviouslyPostedUnverifiedUserReview @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val userAuth: IUserAuth,
    private val orderedBooksRepo: IOrderedBooksRepo,
    private val userReviewRepo: IUserReviewRepo,
): CoroutineWorker(context,
    workerParams
){

    override suspend fun doWork(): Result {

        if (!userAuth.getIsUserAuthenticated()){
           return  Result.success()
        }

            val userId = userAuth.getUserId()
            val orderedBooks = orderedBooksRepo.getOrderedBooks(userId)
            val unVerifiedUserReviews = userReviewRepo.getUserReviews(false)

        if (unVerifiedUserReviews.isEmpty()){
            return Result.success()
        }

           /*For every unverified review look through all ordered books to find which
             review the user has bought its book
            */
           for (review in unVerifiedUserReviews){
               for (book in orderedBooks){
                   if (review.bookId == book.bookId){
                       review.isVerified = true
                       review.postedBefore = true
                   }
               }
           }

               val verifiedReviews = unVerifiedUserReviews.filter {
                   val reviewDoesNotContainSpamLink = !it.review.containsUrl(com.bookshelfhub.core.common.helpers.utils.Regex.WEB_LINK_IN_TEXT)
                   it.isVerified && reviewDoesNotContainSpamLink
               }

        if (verifiedReviews.isEmpty()){
            return Result.success()
        }

           val listOfUpdatedBookValues = mutableListOf<HashMap<String, FieldValue>>()
           for (review in verifiedReviews){
               val dynamicBookAttr =  hashMapOf(
                   RemoteDataFields.TOTAL_REVIEWS to FieldValue.increment(1),
                   RemoteDataFields.TOTAL_RATINGS to FieldValue.increment(review.userRating)
               )
               listOfUpdatedBookValues.add(dynamicBookAttr)
           }

          return try {
               userReviewRepo.updateRemoteUserReviews(
                   verifiedReviews,
                   listOfUpdatedBookValues,
                   userId
               )
               userReviewRepo.addUserReviews(verifiedReviews)
               Result.success()
           }catch (e:Exception){
               ErrorUtil.e(e)
               Result.retry()
           }

    }
}
