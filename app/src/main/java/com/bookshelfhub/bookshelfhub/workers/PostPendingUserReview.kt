package com.bookshelfhub.bookshelfhub.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bookshelfhub.bookshelfhub.helpers.utils.Logger
import com.bookshelfhub.bookshelfhub.helpers.utils.Regex
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.RemoteDataFields
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.IRemoteDataSource
import com.bookshelfhub.bookshelfhub.helpers.database.ILocalDb
import com.google.firebase.firestore.FieldValue
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.tasks.await


@HiltWorker
class PostPendingUserReview @AssistedInject constructor(
    @Assisted val context: Context, @Assisted workerParams: WorkerParameters,
    private val localDb: ILocalDb, private val remoteDataSource: IRemoteDataSource, private val userAuth: IUserAuth
): CoroutineWorker(context,
    workerParams
){

    override suspend fun doWork(): Result {

       return if (userAuth.getIsUserAuthenticated()){

            val userId = userAuth.getUserId()
            val orderedBooks = localDb.getOrderedBooks(userId)
            val unVerifiedUserReviews = localDb.getUserReviews(false)

            //Check for reviews that have its book in Shelf and make it verified
            //Also posted as I will need to add the data to local Db
           if (unVerifiedUserReviews.isNotEmpty()){
               for (review in unVerifiedUserReviews){
                   for (book in orderedBooks){
                       if (review.isbn == book.bookId){
                           review.verified = true
                           review.postedBefore =true
                       }
                   }
               }

               //Get verified reviews from all reviews
               val verifiedReviews = unVerifiedUserReviews.filter {
                   it.verified && !it.review.containsUrl(Regex.WEB_LINK_IN_TEXT)
               }


               if (verifiedReviews.isNotEmpty()){

                   val listOfBookAttr = emptyList<HashMap<String, FieldValue>>()
                   for (review in verifiedReviews){

                       val dynamicBookAttr =  hashMapOf(
                           //Add to book total review
                           RemoteDataFields.TOTAL_REVIEWS.KEY to FieldValue.increment(1),
                           //Add userRatingDiff to total ratings that can be + or -
                           RemoteDataFields.TOTAL_RATINGS.KEY to FieldValue.increment(review.userRating)
                       )
                        listOfBookAttr.plus(dynamicBookAttr)
                   }

                   try {
                       val task = remoteDataSource.updateUserReview(
                           verifiedReviews, RemoteDataFields.PUBLISHED_BOOKS.KEY, RemoteDataFields.REVIEWS_COLL.KEY, userId, listOfBookAttr).await()

                       task.run {
                           localDb.addUserReviews(verifiedReviews)
                           Result.success()
                       }

                   }catch (e:Exception){
                       Logger.log("Worker:PostPendReview", e)
                       Result.retry()
                   }

               }else{
                   Result.success()
               }

           }else{
               Result.success()
           }
        }else{
            Result.success()
        }

    }
}
