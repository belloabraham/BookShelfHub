package com.bookshelfhub.bookshelfhub.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bookshelfhub.bookshelfhub.book.Book
import com.bookshelfhub.bookshelfhub.services.database.cloud.DbFields
import com.bookshelfhub.bookshelfhub.services.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.services.database.cloud.ICloudDb
import com.bookshelfhub.bookshelfhub.services.database.local.ILocalDb
import com.google.firebase.firestore.FieldValue
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject


@HiltWorker
class PostUserReview @AssistedInject constructor(
    @Assisted val context: Context, @Assisted workerParams: WorkerParameters,
    private val localDb: ILocalDb, private val cloudDb: ICloudDb, private val userAuth: IUserAuth
): CoroutineWorker(context,
    workerParams
){

    override suspend fun doWork(): Result {

        val isbn = inputData.getString(Book.ISBN.KEY)!!

        //Get the user review
        val userReview =  localDb.getUserReview(isbn).get()

            val userRatingDiff = inputData.getDouble(Book.RATING_DIFF.KEY,0.0)
            val userId = userAuth.getUserId()

            //Check if the review have been posted before
            val bookTotalReview:Long =  if (userReview.postedBefore){
                //If user is updating a previously posted review
                0
            }else{
                //If user is posting a review for the first time
                1
            }


          val dynamicBookAttr =  if (bookTotalReview>0){ //If user is posting for the first time
                 hashMapOf(
                     //Add to book total review
                     DbFields.TOTAL_REVIEWS.KEY to FieldValue.increment(bookTotalReview),
                     //Add userRatingDiff to total ratings that can be + or -
                     DbFields.TOTAL_RATINGS.KEY to FieldValue.increment(userRatingDiff)
                )
            }else{
              hashMapOf(
                  //Has user has posted before only upload userRatingDiff
                  DbFields.TOTAL_RATINGS.KEY to FieldValue.increment(userRatingDiff)
              )
            }

         val reviewUpdate =  cloudDb.updateUserReview(
                dynamicBookAttr, userReview,
                DbFields.PUBLISHED_BOOKS.KEY, isbn,
                DbFields.REVIEWS.KEY, userId)

        if (reviewUpdate.isSuccessful){
            //Update user review locally
            userReview.postedBefore = true
            localDb.addUserReview(userReview)
        }else{
            return Result.retry()
        }

        return Result.success()
    }
}
