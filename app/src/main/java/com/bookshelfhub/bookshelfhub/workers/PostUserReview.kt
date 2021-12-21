package com.bookshelfhub.bookshelfhub.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.enums.Book
import com.bookshelfhub.bookshelfhub.helpers.Json
import com.bookshelfhub.bookshelfhub.helpers.rest.MediaType
import com.bookshelfhub.bookshelfhub.helpers.rest.WebApi
import com.bookshelfhub.bookshelfhub.models.perspective.response.ResponseBody
import com.bookshelfhub.bookshelfhub.services.database.cloud.DbFields
import com.bookshelfhub.bookshelfhub.services.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.services.database.cloud.ICloudDb
import com.bookshelfhub.bookshelfhub.helpers.database.ILocalDb
import com.bookshelfhub.bookshelfhub.services.remoteconfig.IRemoteConfig
import com.bookshelfhub.bookshelfhub.services.remoteconfig.Keys
import com.bookshelfhub.bookshelfhub.services.wordtoxicity.Perspective
import com.google.firebase.firestore.FieldValue
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import okhttp3.RequestBody.Companion.toRequestBody


@HiltWorker
class PostUserReview @AssistedInject constructor(
    @Assisted val context: Context, @Assisted workerParams: WorkerParameters,
    private val localDb: ILocalDb,
    private val cloudDb: ICloudDb,
    private val webApi:WebApi,
    private val remoteConfig: IRemoteConfig,
    private val json: Json,
    private val userAuth: IUserAuth
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

        var dynamicBookAttr:HashMap<String, FieldValue>?=null

        if (userReview.verified){
            dynamicBookAttr =  if (bookTotalReview>0){ //If user is posting for the first time
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
        }


        val endPointUrl = context.getString(R.string.perspective_api_endpoint)
        val key = remoteConfig.getString(Keys.PERSPECTIVE_API)
        val postBody = Perspective().getPostBody(userReview.review)
        val reqBody = json.getJsonString(postBody).toRequestBody(MediaType.APPLICATION_JSON)

       val response = webApi.post(endPointUrl, key, reqBody)

     return if (response.body!=null){
            val responseBody = json.fromJson(response.body.toString(), ResponseBody::class.java)
            if (responseBody.attributeScores.TOXICITY.summaryScore.value<=0.5){
                val task =  cloudDb.updateUserReview(
                    dynamicBookAttr, userReview,
                    DbFields.PUBLISHED_BOOKS.KEY, isbn,
                    DbFields.REVIEWS.KEY, userId)

                if (task.isSuccessful){
                    if (userReview.verified){
                        //Update user review locally
                        userReview.postedBefore = true
                        localDb.addUserReview(userReview)
                    }
                    Result.success()
                }else{
                     Result.retry()
                }
            }else{
                 Result.success()
            }
      }else{
           Result.retry()
      }

    }

}
