package com.bookshelfhub.bookshelfhub.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bookshelfhub.bookshelfhub.data.repos.orderedbooks.IOrderedBooksRepo
import com.bookshelfhub.bookshelfhub.data.repos.userreview.IUserReviewRepo
import com.bookshelfhub.bookshelfhub.helpers.utils.Regex
import com.bookshelfhub.bookshelfhub.data.sources.remote.RemoteDataFields
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.extensions.containsUrl
import com.google.firebase.firestore.FieldValue
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber


@HiltWorker
class PostPendingUserReview @AssistedInject constructor(
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


           for (review in unVerifiedUserReviews){
               for (book in orderedBooks){
                   if (review.bookId == book.bookId){
                       review.verified = true
                       review.postedBefore =true
                   }
               }
           }

               val verifiedReviews = unVerifiedUserReviews.filter {
                   it.verified && !it.review.containsUrl(Regex.WEB_LINK_IN_TEXT)
               }

        if (verifiedReviews.isEmpty()){
            return Result.success()
        }


           val listOfUpdatedBookValues = emptyList<HashMap<String, FieldValue>>()
           for (review in verifiedReviews){

               val dynamicBookAttr =  hashMapOf(
                   RemoteDataFields.TOTAL_REVIEWS to FieldValue.increment(1),
                   RemoteDataFields.TOTAL_RATINGS to FieldValue.increment(review.userRating)
               )
                listOfUpdatedBookValues.plus(dynamicBookAttr)
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
              Timber.e(e)
               Result.retry()
           }

    }
}
